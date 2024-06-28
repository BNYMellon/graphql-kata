/*
 *   Copyright 2024 The Bank of New York Mellon.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package bny.jpe.graphql.example.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import bny.jpe.graphql.example.entities.ComposerEntity;
import bny.jpe.graphql.example.entities.CompositionEntity;
import bny.jpe.graphql.example.entities.InstrumentEntity;
import bny.jpe.graphql.example.entities.LocationEntity;
import org.springframework.graphql.data.GraphQlRepository;
import org.springframework.transaction.annotation.Transactional;

import bny.jpe.graphql.kata.domain.Composer;
import bny.jpe.graphql.kata.domain.ComposerInput;
import bny.jpe.graphql.kata.domain.Composition;
import bny.jpe.graphql.kata.domain.CompositionInput;
import bny.jpe.graphql.kata.domain.Concerto;
import bny.jpe.graphql.kata.domain.Instrument;
import bny.jpe.graphql.kata.domain.Location;
import bny.jpe.graphql.kata.domain.Song;

@GraphQlRepository
public class ComposerRepository
{
    @PersistenceContext
    private EntityManager manager;

    public List<Composer> allComposers()
    {
        List<ComposerEntity> resultList = manager.createQuery("""
                                                                      SELECT DISTINCT c
                                                                      FROM ComposerEntity c
                                                                      LEFT JOIN FETCH c.compositions
                                                                      """,
                                                              ComposerEntity.class)
                .getResultList();
        return resultList.stream().map(ComposerEntity::toRecord).collect(Collectors.toList());
    }

    @Transactional
    public Composer addComposer(ComposerInput composer)
    {
        ComposerEntity entity = initializeEntity(new ComposerEntity(), composer);
        manager.persist(entity);
        return entity.toRecord();
    }

    @Transactional
    public Composer updateComposer(int id, ComposerInput composer)
    {
        ComposerEntity entity = initializeEntity(manager.find(ComposerEntity.class, id), composer);
        manager.persist(entity);
        return entity.toRecord();
    }

    @Transactional
    public List<Composition> addCompositions(int composerId, List<CompositionInput> compositions)
    {
        List<CompositionEntity> entities = compositions.stream()
                .map(c -> insertAsEntity(composerId, c))
                .collect(Collectors.toList());
        return entities.stream().map(CompositionEntity::toRecord).collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteComposition(int composerId, String title)
    {
        CompositionEntity entity = manager.createQuery("""
                                                               SELECT DISTINCT comp
                                                               FROM CompositionEntity comp
                                                               WHERE comp.title = :title
                                                               AND comp.composer.id = :composerId""",
                                                       CompositionEntity.class)
                .setParameter("composerId", composerId).setParameter("title", title)
                .getSingleResult();
        manager.remove(entity);
        return true;
    }

    private ComposerEntity initializeEntity(ComposerEntity entity, ComposerInput composer)
    {
        Optional.ofNullable(composer.firstName()).ifPresent(fn -> entity.firstName = fn);
        Optional.ofNullable(composer.lastName()).ifPresent(ln -> entity.lastName = ln);
        Optional.ofNullable(composer.dateOfBirth()).ifPresent(d -> entity.dateOfBirth = d);
        Optional.ofNullable(composer.period()).ifPresent(p -> entity.period = p);
        Optional.ofNullable(composer.location())
                .ifPresent(l -> entity.location = existingOrNew(l, this::findLocation, this::createLocation));
        Optional.ofNullable(composer.primaryInstrument())
                .ifPresent(i -> entity.primaryInstrument = existingOrNew(i,
                                                                         this::findInstrument,
                                                                         this::createInstrument));
        entity.compositions = Collections.emptyList();
        return entity;
    }

    private <T, R> R existingOrNew(T entity, Function<T, R> find, Function<T, R> create)
    {
        try
        {
            return find.apply(entity);
        }
        catch (NoResultException e)
        {
            return create.apply(entity);
        }
    }

    private LocationEntity findLocation(Location location)
    {
        return manager.createQuery("""
                                           SELECT DISTINCT loc
                                           FROM LocationEntity loc
                                           WHERE loc.city = :city AND loc.country = :country""",
                                   LocationEntity.class)
                .setParameter("city", location.city())
                .setParameter("country", location.country())
                .getSingleResult();
    }

    private InstrumentEntity findInstrument(Instrument instrument)
    {
        return manager.createQuery("""
                                           SELECT DISTINCT inst
                                           FROM InstrumentEntity inst
                                           WHERE inst.name = :name AND inst.type = :type""",
                                   InstrumentEntity.class)
                .setParameter("name", instrument.name())
                .setParameter("type", instrument.type())
                .getSingleResult();
    }

    private LocationEntity createLocation(Location location)
    {
        LocationEntity locEnt = new LocationEntity();
        locEnt.city = location.city();
        locEnt.country = location.country();
        manager.persist(locEnt);
        return locEnt;
    }

    private InstrumentEntity createInstrument(Instrument instrument)
    {
        InstrumentEntity instrumentEntity = new InstrumentEntity();
        instrumentEntity.name = instrument.name();
        instrumentEntity.type = instrument.type();
        manager.persist(instrumentEntity);
        return instrumentEntity;
    }

    private CompositionEntity insertAsEntity(int composerId, CompositionInput comp)
    {
        CompositionEntity entity = new CompositionEntity();
        entity.composer = manager.find(ComposerEntity.class, composerId);
        entity.subtype = comp.getClass().getSimpleName();
        entity.title = comp.title();
        entity.keyCenter = comp.key();

        entity.soloInstrument = Concerto.class.getSimpleName().equals(comp.subtype())
                ? existingOrNew(comp.soloInstrument(), this::findInstrument, this::createInstrument)
                : null;
        entity.lyrics = Song.class.getSimpleName().equals(comp.subtype()) ? comp.lyrics() : null;
        manager.persist(entity);
        return entity;
    }
}
