/*
 *   Copyright 2023 The Bank of New York Mellon.
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

package bnymellon.jpe.graphql.example.controllers;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.ListAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import bnymellon.jpe.graphql.example.dao.ComposerRepository;
import bnymellon.jpe.graphql.kata.domain.Composer;
import bnymellon.jpe.graphql.kata.domain.ComposerInput;
import bnymellon.jpe.graphql.kata.domain.Composition;
import bnymellon.jpe.graphql.kata.domain.CompositionInput;
import bnymellon.jpe.graphql.kata.domain.Period;
import graphql.GraphqlErrorException;
import graphql.execution.DataFetcherResult;

@Controller
public class ComposersController
{
    private final ComposerRepository dao;

    @Autowired
    public ComposersController(ComposerRepository dao)
    {
        this.dao = dao;
    }

    @QueryMapping
    public Composer composer(@Argument int id)
    {
        return ListAdapter.adapt(dao.allComposers()).detect(c -> id == c.id());
    }

    @QueryMapping
    public List<Composer> composers(@Argument Period period,
                                    @Argument String country,
                                    @Argument String city,
                                    @Argument String instrument,
                                    @Argument String lastName)
    {
        return selectIfPresent(period, Composer::period)
                .andThen(selectIfPresent(country, c -> c.location().country()))
                .andThen(selectIfPresent(city, c -> c.location().city()))
                .andThen(selectIfPresent(instrument, c -> c.primaryInstrument().name()))
                .andThen(selectIfPresent(lastName, Composer::lastName))
                .apply(Lists.adapt(dao.allComposers()));
    }

    @MutationMapping
    public Composer createComposer(@Argument ComposerInput composer)
    {
        return dao.addComposer(composer);
    }

    @MutationMapping
    public Composer updateComposer(@Argument int id, @Argument ComposerInput composer)
    {
        return dao.updateComposer(id, composer);
    }

    @MutationMapping
    public List<Composition> addCompositions(@Argument int composerId, @Argument List<CompositionInput> compositions)
    {
        return dao.addCompositions(composerId, compositions);
    }

    @MutationMapping
    public DataFetcherResult<Boolean> deleteComposition(@Argument int composerId, @Argument String title)
    {
        try
        {
            return DataFetcherResult.<Boolean>newResult().data(dao.deleteComposition(composerId, title)).build();
        }
        catch (EmptyResultDataAccessException e)
        {
            return DataFetcherResult.<Boolean>newResult().data(false)
                    .error(GraphqlErrorException.newErrorException().message(e.getMessage()).build()).build();
        }
    }

    private <T, R> UnaryOperator<MutableList<T>> selectIfPresent(R param, Function<T, R> getter)
    {
        return list -> Objects.nonNull(param) ? list.select(c -> param.equals(getter.apply(c))) : list;
    }

    @SchemaMapping
    public List<Composition> compositions(Composer composer, @Argument String key, @Argument String subtype)
    {
        return selectIfPresent(subtype, (Composition comp) -> comp.getClass().getSimpleName())
                .andThen(selectIfPresent(key, Composition::key))
                .apply(Lists.adapt(composer.compositions()));
    }
}
