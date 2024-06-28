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

package bny.jpe.graphql.kata.mutation;

import java.io.IOException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import bny.jpe.graphql.kata.GraphQLBaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * For each test, execute a GraphQL mutation which will perform the proper update and make the test pass.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MutateComposersTest
        extends GraphQLBaseTest
{
    /**
     * Just like queries, GraphQL mutations take input arguments and return a customizable response.
     * The GraphQL type system supports distinct input types which allows validation of arguments against the schema.
     * Add the missing fields to the inline ComposerInput argument of the mutation in "src/test/resources/mutations/add-composer.graphql".
     * <p>
     * Hint: See the schema at "graphql-composers-domain/src/main/resources/schema.graphqls" for a description of mutations and input types.
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/queries/#mutations">GraphQL Mutations</a></li>
     * <li><a href="https://graphql.org/learn/schema/#input-types">GraphQL Input Types</a></li>
     * <li><a href="https://graphql.org/learn/queries/#arguments">GraphQL Arguments</a></li>
     * </ul>
     */
    @Test
    @Order(1)
    public void addComposer() throws IOException
    {
        assertEquals("""
                             {
                               "data" : {
                                 "createComposer" : {
                                   "id" : "9"
                                 }
                               }
                             }""",
                     executeOperation("/mutations/add-composer.graphql"));
        assertEquals(
                // TODO: Use this expected query response to fill in the input argument values.
                """
                        {
                          "data" : {
                            "composer" : {
                              "firstName" : "Charles",
                              "lastName" : "Ives",
                              "dateOfBirth" : "1874-10-20",
                              "location" : {
                                "city" : "Danbury",
                                "country" : "United States"
                              }
                            }
                          }
                        }""",
                executeOperation("/queries/get-composer-by-id.graphql", """
                        {
                           "id" : "9",
                           "withBirthday" : true
                        }"""));
    }

    /**
     * GraphQL mutations can be used for updates as well as creation/insertion.
     * Depending on the server-side implementation, mutations can also return partial results with errors.
     * Add variables to the named operation in "src/test/resources/mutations/update-composer.graphql"
     * and modify the JSON input variables below.
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/queries/#mutations">GraphQL Mutations</a></li>
     * <li><a href="https://graphql.org/learn/schema/#input-types">GraphQL Input Types</a></li>
     * <li><a href="https://graphql.org/learn/queries/#arguments">GraphQL Arguments</a></li>
     * </ul>
     */
    @Test
    @Order(2)
    public void updateLocationAndDateOfBirth() throws IOException
    {
        assertEquals(
                // Notice the results.
                """
                        {
                          "errors" : [
                            {
                              "message" : "You cannot change an existing date of birth.",
                              "locations" : [
                                {
                                  "line" : 16,
                                  "column" : 46
                                }
                              ],
                              "extensions" : {
                                "classification" : "ValidationError"
                              }
                            }
                          ],
                          "data" : {
                            "updateComposer" : {
                              "id" : "9"
                            }
                          }
                        }""",
                executeOperation("/mutations/update-composer.graphql",
                                 // TODO: Add a location to this update based on the expected response.
                                 // See what happens when you try to modify an existing dateOfBirth.
                                 """
                                         {
                                           "id" : "9",
                                           "composer" : {
                                               "lastName" : "Ives",
                                               "dateOfBirth": "2021-11-04",
                                               "location" : { "city" : "New York", "country" : "United States" }
                                           }
                                         }"""));
        assertEquals(
                // TODO: Use this expected query response to fill in the input argument values.
                // Notice the location and dateOfBirth values after the attempted update.
                """
                        {
                          "data" : {
                            "composer" : {
                              "firstName" : "Charles",
                              "lastName" : "Ives",
                              "dateOfBirth" : "1874-10-20",
                              "location" : {
                                "city" : "New York",
                                "country" : "United States"
                              }
                            }
                          }
                        }""",
                executeOperation("/queries/get-composer-by-id.graphql", """
                        {
                           "id" : "9",
                           "withBirthday" : true
                        }"""));
    }

    /**
     * GraphQL Mutations can also be written as named operations with dynamic input variables.
     * Convert the mutation in "src/test/resources/mutations/add-compositions.graphql" to a named operation
     * and add the missing "composerId" and "compositions" parameters as both an operation variables
     * and as mutation arguments.
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/queries/#operation-name">GraphQL Operation Names</a></li>
     * </ul>
     */
    @Test
    @Order(3)
    public void addCompositions() throws IOException
    {
        assertEquals("""
                             {
                               "data" : {
                                 "addCompositions" : [
                                   {
                                     "__typename" : "Piece",
                                     "title" : "Central Park in the Dark"
                                   },
                                   {
                                     "__typename" : "Concerto",
                                     "title" : "Emerson Concerto"
                                   }
                                 ]
                               }
                             }""",
                     executeOperation("/mutations/add-compositions.graphql",
                                      // Notice the input variables already provided to be sent with the mutation.
                                      """
                                              {
                                                 "composerId" : "9",
                                                 "compositions" : [{
                                                         "subtype" : "Piece",
                                                         "title" : "Central Park in the Dark",
                                                         "key" : "A#m"
                                                     },{
                                                         "subtype" : "Concerto",
                                                         "title" : "Emerson Concerto",
                                                         "key" : "F",
                                                         "soloInstrument" : {
                                                             "name" : "Piano",
                                                             "type" : "KEYBOARD"
                                                         }
                                                     }]
                                              }"""));
        assertEquals("""
                             {
                               "data" : {
                                 "composers" : [
                                   {
                                     "firstName" : "Charles",
                                     "lastName" : "Ives",
                                     "compositions" : [
                                       {
                                         "__typename" : "Piece",
                                         "title" : "Central Park in the Dark",
                                         "key" : "A#m"
                                       },
                                       {
                                         "__typename" : "Concerto",
                                         "title" : "Emerson Concerto",
                                         "key" : "F",
                                         "soloInstrument" : {
                                           "name" : "Piano",
                                           "type" : "KEYBOARD"
                                         }
                                       }
                                     ]
                                   }
                                 ]
                               }
                             }""",
                     executeOperation("/queries/get-composers-compositions.graphql", """
                             {
                                "lastName" : "Ives"
                             }"""));
    }

    /**
     * Finally, GraphQL mutations are used for deleting data.
     * All the principles used in "create" and "update" mutations apply here as well.
     * Make the appropriate modifications to "src/test/resources/mutations/delete-composition.graphql".
     * Update the JSON input variables below to delete "Emerson Concerto" by Charles Ives.
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/queries/#mutations">GraphQL Mutations</a></li>
     * </ul>
     */
    @Test
    @Order(4)
    public void deleteComposition() throws IOException
    {
        assertEquals("""
                             {
                               "data" : {
                                 "deleteComposition" : true
                               }
                             }""",
                     executeOperation("/mutations/delete-composition.graphql",
                                      // TODO: Add "composerId" and "title" variables.
                                      """
                                              {
                                                 "composerId" : "9",
                                                 "title" : "Emerson Concerto"
                                              }"""));
        assertEquals("""
                             {
                               "data" : {
                                 "composers" : [
                                   {
                                     "firstName" : "Charles",
                                     "lastName" : "Ives",
                                     "compositions" : [
                                       {
                                         "__typename" : "Piece",
                                         "title" : "Central Park in the Dark",
                                         "key" : "A#m"
                                       }
                                     ]
                                   }
                                 ]
                               }
                             }""",
                     executeOperation("/queries/get-composers-compositions.graphql", """
                             {
                                "lastName" : "Ives"
                             }"""));
    }
}
