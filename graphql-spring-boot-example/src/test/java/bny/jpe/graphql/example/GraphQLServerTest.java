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

package bny.jpe.graphql.example;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.test.tester.GraphQlTester;

import bny.jpe.graphql.kata.domain.Composer;
import bny.jpe.graphql.kata.domain.ComposerInput;
import bny.jpe.graphql.kata.domain.Composition;
import bny.jpe.graphql.kata.domain.CompositionInput;
import bny.jpe.graphql.kata.domain.Instrument;
import bny.jpe.graphql.kata.domain.InstrumentType;
import bny.jpe.graphql.kata.domain.Location;

import static bny.jpe.graphql.kata.domain.Period.POST_MODERN;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureHttpGraphQlTester
public class GraphQLServerTest
{
    @Autowired
    GraphQlTester tester;

    private Integer addedComposerId;

    @Test
    @Order(0)
    public void getAllComposers()
    {
        tester.documentName("composers-query").execute()
                .path("composers").entityList(Composer.class)
                .hasSize(8);
    }

    @Test
    @Order(0)
    public void getComposerById()
    {
        tester.documentName("get-composer-by-id").variable("id", "2").execute()
                .path("composer").entity(Composer.class)
                .satisfies(c -> assertEquals("Beethoven", c.lastName()));
    }

    @Test
    @Order(0)
    public void getPeriodComposers()
    {
        tester.documentName("composers-query").variable("period", "RENAISSANCE").execute()
                .path("composers").entityList(Composer.class)
                .hasSize(1)
                .matches(l -> l.stream().allMatch(c -> "Palestrina".equals(c.lastName())));
    }

    @Test
    @Order(0)
    public void getComposersByLocation()
    {
        tester.documentName("composers-query").variable("city", "Vienna").execute()
                .path("composers").entityList(Composer.class)
                .hasSize(2)
                .matches(l -> l.stream().anyMatch(c -> "Beethoven".equals(c.lastName())))
                .matches(l -> l.stream().anyMatch(c -> "Mozart".equals(c.lastName())));

        tester.documentName("composers-query").variable("country", "Russia").execute()
                .path("composers").entityList(Composer.class)
                .hasSize(1)
                .matches(l -> l.stream().anyMatch(c -> "Rachmaninoff".equals(c.lastName())));
    }

    @Test
    @Order(0)
    public void getPianists()
    {
        tester.documentName("composers-query").variable("instrument", "Piano").execute()
                .path("composers").entityList(Composer.class)
                .hasSize(5)
                .matches(l -> l.stream().anyMatch(c -> "Beethoven".equals(c.lastName())))
                .matches(l -> l.stream().anyMatch(c -> "Schumann".equals(c.lastName())))
                .matches(l -> l.stream().anyMatch(c -> "Rachmaninoff".equals(c.lastName())))
                .matches(l -> l.stream().anyMatch(c -> "Ravel".equals(c.lastName())))
                .matches(l -> l.stream().anyMatch(c -> "Cage".equals(c.lastName())));
    }

    @Test
    @Order(0)
    public void getCompositionsInCMinor()
    {
        tester.documentName("compositions-query").variable("key", "Cm").execute()
                .path("composers[*].compositions")
                .entityList(new ParameterizedTypeReference<List<Composition>>()
                {
                })
                .matches(l -> l.stream().flatMap(List::stream).count() == 2)
                .matches(l -> l.stream().flatMap(List::stream).anyMatch(c -> "Symphony No. 5".equals(c.title())))
                .matches(l -> l.stream().flatMap(List::stream).anyMatch(c -> "Piano Concerto No. 2".equals(c.title())));
    }

    @Test
    @Order(1)
    public void addComposer()
    {
        ComposerInput input = new ComposerInput("Charles",
                                                "Ives",
                                                LocalDate.of(1874, 10, 20),
                                                new Location("Danbury", "United States"),
                                                new Instrument("Piano", InstrumentType.KEYBOARD),
                                                POST_MODERN);

        addedComposerId = tester.documentName("add-composer")
                .variable("composer", input)
                .execute()
                .path("createComposer.id")
                .entity(Integer.class)
                .get();

        tester.documentName("get-composer-by-id")
                .variable("id", addedComposerId)
                .execute()
                .path("composer")
                .entity(Composer.class)
                .matches(c -> "Ives".equals(c.lastName()))
                .matches(c -> "Danbury".equals(c.location().city()));
    }

    @Test
    @Order(2)
    public void updateComposer()
    {
        ComposerInput input = new ComposerInput(null, "Ives", null, new Location("New York", "United States"), null, null);
        tester.documentName("update-composer")
                .variable("composer", input)
                .variable("id", addedComposerId)
                .execute()
                .path("updateComposer.id")
                .entity(Integer.class)
                .matches(addedComposerId::equals);

        tester.documentName("get-composer-by-id")
                .variable("id", addedComposerId)
                .execute()
                .path("composer")
                .entity(Composer.class)
                .matches(c -> "Ives".equals(c.lastName()))
                .matches(c -> "New York".equals(c.location().city()));
    }

    @Test
    @Order(3)
    public void addCompositions()
    {
        CompositionInput input = new CompositionInput("Piece", "Central Park in the Dark", "A#m", null, null);

        tester.documentName("add-compositions")
                .variable("composerId", addedComposerId)
                .variable("compositions", List.of(input))
                .execute()
                .path("addCompositions")
                .entityList(Composition.class)
                .matches(l -> l.stream().anyMatch(c -> "Central Park in the Dark".equals(c.title())));


        tester.documentName("compositions-query")
                .variable("lastName", "Ives")
                .execute()
                .path("composers[*].compositions")
                .entityList(new ParameterizedTypeReference<List<Composition>>()
                {
                })
                .matches(l -> l.stream().flatMap(List::stream)
                        .anyMatch(c -> "Central Park in the Dark".equals(c.title())));
    }

    @Test
    @Order(4)
    public void deleteComposition()
    {
        tester.documentName("delete-composition")
                .variable("composerId", addedComposerId)
                .variable("title", "Central Park in the Dark")
                .executeAndVerify();

        tester.documentName("compositions-query")
                .variable("lastName", "Ives")
                .execute()
                .path("composers[*].compositions")
                .entityList(new ParameterizedTypeReference<List<Composition>>()
                {
                })
                .matches(l -> l.stream().mapToLong(List::size).sum() == 0);
    }
}
