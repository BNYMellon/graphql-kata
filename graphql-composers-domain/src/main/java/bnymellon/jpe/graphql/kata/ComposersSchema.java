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

package bnymellon.jpe.graphql.kata;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.ListAdapter;

import bnymellon.jpe.graphql.kata.domain.Composer;
import bnymellon.jpe.graphql.kata.domain.Composition;
import bnymellon.jpe.graphql.kata.domain.Concerto;
import bnymellon.jpe.graphql.kata.domain.Instrument;
import bnymellon.jpe.graphql.kata.domain.Location;
import bnymellon.jpe.graphql.kata.domain.Piece;
import bnymellon.jpe.graphql.kata.domain.Song;
import graphql.com.google.common.base.Function;
import graphql.com.google.common.base.Supplier;
import graphql.execution.DataFetcherResult;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.validation.ValidationError;
import graphql.validation.ValidationErrorType;

import static bnymellon.jpe.graphql.kata.domain.InstrumentType.KEYBOARD;
import static bnymellon.jpe.graphql.kata.domain.InstrumentType.WOODWIND;
import static bnymellon.jpe.graphql.kata.domain.Period.BAROQUE;
import static bnymellon.jpe.graphql.kata.domain.Period.CLASSICAL;
import static bnymellon.jpe.graphql.kata.domain.Period.MODERN;
import static bnymellon.jpe.graphql.kata.domain.Period.POST_MODERN;
import static bnymellon.jpe.graphql.kata.domain.Period.RENAISSANCE;
import static bnymellon.jpe.graphql.kata.domain.Period.ROMANTIC;

public class ComposersSchema
{
    public static final Instrument ORGAN = new Instrument("Organ", KEYBOARD);
    public static final Instrument PIANO = new Instrument("Piano", KEYBOARD);
    public static final Instrument HARPSICHORD = new Instrument("Harpsichord", KEYBOARD);
    public static final Instrument CLARINET = new Instrument("Clarinet", WOODWIND);

    private final MutableList<Composer> composers = Lists.mutable
            .of(new Composer(1,
                             "Johann",
                             "Bach",
                             LocalDate.of(1685, 3, 31),
                             new Location("Leipzig", "Germany"),
                             ORGAN,
                             BAROQUE,
                             Lists.mutable.of(new Piece("Toccata and Fugue in D minor", "Dm"),
                                              new Piece("Mass in B minor", "Bm"),
                                              new Piece("Cello Suite No. 1", "G"))),
                new Composer(2,
                             "Ludwig",
                             "Beethoven",
                             LocalDate.of(1770, 3, 26),
                             new Location("Vienna", "Austria"),
                             PIANO,
                             CLASSICAL,
                             Lists.mutable.of(new Piece("Symphony No. 5", "Cm"),
                                              new Piece("Moonlight Sonata", "C#m"))),
                new Composer(3,
                             "Wolfgang",
                             "Mozart",
                             LocalDate.of(1756, 1, 27),
                             new Location("Vienna", "Austria"),
                             HARPSICHORD,
                             CLASSICAL,
                             Lists.mutable.of(new Piece("Eine kleine Nachtmusik", "G"),
                                              new Piece("Requiem", "Dm"),
                                              new Concerto("Clarinet Concerto in A Major", "A", CLARINET))),
                new Composer(4,
                             "Clara",
                             "Schumann",
                             LocalDate.of(1819, 9, 13),
                             new Location("Frankfurt", "Germany"),
                             PIANO,
                             ROMANTIC,
                             Lists.mutable.of(new Concerto("Piano Concerto in A minor", "Am", PIANO),
                                              new Song("Liebesfrühling", "C#", """
                                                      How often has spring returned
                                                      For the dead and desolate world!
                                                      How often was spring greeted on all sides
                                                      By happy songs in wood and field!
                                                                                                            
                                                      How often has spring returned!
                                                      But no spring burgeoned for me:
                                                      The songs of my heart are silent,
                                                      For spring can only be brought by you."""))),
                new Composer(5,
                             "Giovanni",
                             "Palestrina",
                             LocalDate.of(1594, 2, 2),
                             new Location("Rome", "Italy"),
                             ORGAN,
                             RENAISSANCE,
                             Lists.mutable.of(new Piece("Missa Papae Marcelli", "C"))),
                new Composer(6,
                             "Sergei",
                             "Rachmaninoff",
                             LocalDate.of(1873, 4, 1),
                             new Location("Moscow", "Russia"),
                             PIANO,
                             ROMANTIC,
                             Lists.mutable.of(new Concerto("Piano Concerto No. 2", "Cm", PIANO),
                                              new Piece("Symphony No. 2", "Em"))),
                new Composer(7,
                             "Maurice",
                             "Ravel",
                             LocalDate.of(1875, 3, 7),
                             new Location("Paris", "France"),
                             PIANO,
                             MODERN,
                             Lists.mutable.of(new Piece("Bolero", "C"),
                                              new Piece("La Valse", "D"))),
                new Composer(8,
                             "John",
                             "Cage",
                             LocalDate.of(1912, 9, 5),
                             new Location("New York", "America"),
                             PIANO,
                             POST_MODERN,
                             Lists.mutable.of(new Piece("4:33", null),
                                              new Piece("Organ² / ASLSP", "Bb")))
            );

    public GraphQLSchema initSchema()
    {
        SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        TypeDefinitionRegistry typeRegistry = schemaParser.parse(this.getClass()
                                                                         .getResourceAsStream("/graphql/schema.graphqls"));
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .scalar(GraphQLScalarType.newScalar().name("Date").coercing(new CoercingDate()).build())
                .scalar(GraphQLScalarType.newScalar().name("Key").coercing(new CoercingKey()).build())
                .type("Composition", builder -> builder
                        .typeResolver(env -> env.getSchema().getObjectType(env.getObject().getClass().getSimpleName())))
                .type("Composer", builder -> builder.dataFetcher("compositions", this::getComposerCompositions))
                .type("Query", builder -> builder
                        .dataFetcher("composer", this::getComposer)
                        .dataFetcher("composers", this::getComposers))
                .type("Mutation", builder -> builder
                        .dataFetcher("createComposer", this::addComposer)
                        .dataFetcher("updateComposer", this::updateComposer)
                        .dataFetcher("addCompositions", this::addCompositions)
                        .dataFetcher("deleteComposition", this::deleteComposition))
                .build();
        return schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
    }

    private Composer getComposer(DataFetchingEnvironment env)
    {
        return composers.select(c -> Integer.parseInt(env.getArgument("id")) == c.id()).getFirstOptional().get();
    }

    private List<Composer> getComposers(DataFetchingEnvironment env)
    {
        return composers.asLazy()
                .select(comparingIfArg(env.getArgument("period"), c -> c.period().name()))
                .select(comparingIfArg(env.getArgument("country"), c -> c.location().country()))
                .select(comparingIfArg(env.getArgument("city"), c -> c.location().city()))
                .select(comparingIfArg(env.getArgument("instrument"), c -> c.primaryInstrument().name()))
                .select(comparingIfArg(env.getArgument("lastName"), c -> c.lastName()))
                .toList();
    }

    private Predicate<Composer> comparingIfArg(String arg1, Function<Composer, String> function)
    {
        return (composer) -> Objects.isNull(arg1) || arg1.isBlank() || arg1.equals(function.apply(composer));
    }

    private List<Composition> getComposerCompositions(DataFetchingEnvironment environment)
    {
        Composer composer = environment.getSource();
        String type = environment.getArgument("subtype");
        return Objects.isNull(type) || type.isBlank()
                ? composer.compositions()
                : composer.compositions(composition -> type.equals(composition.getClass().getSimpleName()));
    }

    private Object addComposer(DataFetchingEnvironment environment)
    {
        Composer composer = Composer
                .fromInput(composers.collectInt(Composer::id).max() + 1, environment.getArgument("composer"));
        composers.add(composer);
        return composer;
    }

    private Object updateComposer(DataFetchingEnvironment environment)
    {
        int id = Integer.parseInt(environment.getArgument("id"));
        Composer existing = composers.selectWith(this::matchesId, id).getFirstOptional().get();
        Composer input = Composer.fromInput(id, environment.getArgument("composer"));

        Composer updated = new Composer(id,
                                        resolve(input::firstName, existing::firstName),
                                        resolve(input::lastName, existing::lastName),
                                        // Only update dateOfBirth if existing is null.
                                        resolve(existing::dateOfBirth, input::dateOfBirth),
                                        resolveLocation(input.location(), existing.location()),
                                        resolveInstrument(input.primaryInstrument(), existing.primaryInstrument()),
                                        resolve(input::period, existing::period),
                                        Lists.mutable.empty());
        composers.removeIfWith(this::matchesId, id);
        composers.add(updated);
        DataFetcherResult.Builder<Object> data = DataFetcherResult.newResult().data(updated);
        if (!input.dateOfBirth().equals(existing.dateOfBirth()))
        {
            SourceLocation sourceLocation = environment.getOperationDefinition()
                    .getVariableDefinitions().get(1).getType().getSourceLocation();
            return data.error(ValidationError.newValidationError()
                                      .validationErrorType(ValidationErrorType.UnusedVariable)
                                      .sourceLocation(sourceLocation)
                                      .description("You cannot change an existing date of birth.").build()).build();
        }
        return data.build();
    }

    private List<Composition> addCompositions(DataFetchingEnvironment environment)
    {
        int composerId = Integer.parseInt(environment.getArgument("composerId"));
        List<Map> comps = environment.getArgument("compositions");
        MutableList<Composition> compositions = ListAdapter.adapt(comps).collect(this::mapComposition);
        composers.detectWith(this::matchesId, composerId).compositions().addAll(compositions);
        return compositions;
    }

    private Object deleteComposition(DataFetchingEnvironment environment)
    {
        int composerId = Integer.parseInt(environment.getArgument("composerId"));
        String title = environment.getArgument("title");
        return composers.detectWith(this::matchesId, composerId).compositions()
                .removeIf(composition -> title.equals(composition.title()));
    }

    private Composition mapComposition(Map input)
    {
        return switch ((String) input.get("subtype"))
                {
                    case "Piece" -> Piece.fromInput(input);
                    case "Concerto" -> Concerto.fromInput(input);
                    case "Song" -> Song.fromInput(input);
                    default -> null;
                };
    }

    private boolean matchesId(Composer composer, int id)
    {
        return id == composer.id();
    }

    private <T> T resolve(Supplier<T> update, Supplier<T> existing)
    {
        return Optional.ofNullable(update.get()).orElse(existing.get());
    }

    private Location resolveLocation(Location update, Location existing)
    {
        return Objects.nonNull(update)
                ? new Location(resolve(update::city, existing::city), resolve(update::country, existing::country))
                : existing;
    }

    private Instrument resolveInstrument(Instrument update, Instrument existing)
    {
        return Objects.nonNull(update)
                ? new Instrument(resolve(update::name, existing::name), resolve(update::type, existing::type))
                : existing;
    }
}
