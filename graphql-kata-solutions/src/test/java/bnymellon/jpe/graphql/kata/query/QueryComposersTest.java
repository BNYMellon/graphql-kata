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

package bnymellon.jpe.graphql.kata.query;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import bnymellon.jpe.graphql.kata.GraphQLBaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * For each test, execute a GraphQL query which will return the expected response and make the test pass.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueryComposersTest
        extends GraphQLBaseTest
{
    /**
     * GraphQL allows a client to customize which fields are included in the response data.
     * View the JSON test output for the query contained in "src/test/resources/queries/get-all-composers.graphql".
     * Try adding or removing some fields to the query and notice how the response changes.
     * <p>
     * Hint: See the schema at "graphql-composers-domain/src/main/resources/schema.graphqls" for a description of types and their fields
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/queries/#fields">GraphQL Fields</a></li>
     * </ul>
     */
    @Test
    public void queryAllComposers() throws IOException
    {
        // Check the console output to see the JSON response to the query.
        System.out.println("COMPOSERS: " + executeOperation("/queries/get-all-composers.graphql"));
    }


    /**
     * A client can also pass arguments in a query to perform server-side filtering.
     * Add the missing argument in "src/test/resources/queries/get-modern-composers.graphql".
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/queries/#arguments">GraphQL Arguments</a></li>
     * <li><a href="https://graphql.org/learn/schema/#enumeration-types">GraphQL Enumerations</a></li>
     * </ul>
     */
    @Test
    public void queryModernComposers() throws IOException
    {
        assertEquals("""
                             {
                               "data" : {
                                 "composers" : [
                                   {
                                     "firstName" : "Maurice",
                                     "lastName" : "Ravel",
                                     "period" : "MODERN"
                                   }
                                 ]
                               }
                             }""",
                     executeOperation("/queries/get-modern-composers.graphql"));
    }

    /**
     * By using aliases, a client can request data in a single response for the same query with different arguments.
     * Add the missing aliases in "src/test/resources/queries/get-period-composers.graphql".
     * Remove any duplication in the query by using GraphQL fragments.
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/queries/#aliases">GraphQL Query Aliases</a></li>
     * <li><a href="https://graphql.org/learn/queries/#fragments">GraphQL Fragments</a></li>
     * <li><a href="https://graphql.org/learn/schema/#enumeration-types">GraphQl Enumerations</a></li>
     * </ul>
     */
    @Test
    public void queryRenaissanceAndBaroqueComposers() throws IOException
    {
        assertEquals("""
                             {
                               "data" : {
                                 "renaissanceComposers" : [
                                   {
                                     "firstName" : "Giovanni",
                                     "lastName" : "Palestrina",
                                     "period" : "RENAISSANCE"
                                   }
                                 ],
                                 "baroqueComposers" : [
                                   {
                                     "firstName" : "Johann",
                                     "lastName" : "Bach",
                                     "period" : "BAROQUE"
                                   }
                                 ]
                               }
                             }""",
                     executeOperation("/queries/get-period-composers.graphql"));
    }

    /**
     * Query arguments can be passed dynamically as variables by an operation.
     * Add the missing "id" parameter as both an operation variable
     * and a query argument in "src/test/resources/queries/get-composer-by-id.graphql"
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/queries/#operation-name">GraphQL Operation Names</a></li>
     * <li><a href="https://graphql.org/learn/queries/#variables">GraphQL Variables</a></li>
     * <li><a href="https://graphql.org/learn/queries/#arguments">GraphQL Arguments</a></li>
     * </ul>
     */
    @Test
    public void queryComposerById() throws IOException
    {
        assertEquals("""
                             {
                               "data" : {
                                 "composer" : {
                                   "firstName" : "Johann",
                                   "lastName" : "Bach",
                                   "location" : {
                                     "city" : "Leipzig",
                                     "country" : "Germany"
                                   }
                                 }
                               }
                             }""",
                     executeOperation("/queries/get-composer-by-id.graphql",
                                      // Notice the variables sent with the query.
                                      """
                                              {
                                                "id": 1
                                              }"""));
    }

    /**
     * Directives allow a client to programmatically include fields in a query response.
     * Add a directive for the composer's birthday in "src/test/resources/queries/get-composer-by-id.graphql"
     * Notice how a query can easily be used to retrieve different results based on dynamic input.
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/queries/#default-variables">GraphQL Default Variables</a></li>
     * <li><a href="https://graphql.org/learn/queries/#directives">GraphQL Directives</a></li>
     * <li><a href="https://graphql.org/learn/schema/#scalar-types">GraphQL Scalar Types</a></li>
     * </ul>
     */
    @Test
    public void queryComposerBirthday() throws IOException
    {
        assertEquals("""
                             {
                               "data" : {
                                 "composer" : {
                                   "firstName" : "Johann",
                                   "lastName" : "Bach",
                                   "dateOfBirth" : "1685-03-31",
                                   "location" : {
                                     "city" : "Leipzig",
                                     "country" : "Germany"
                                   }
                                 }
                               }
                             }""",
                     executeOperation("/queries/get-composer-by-id.graphql",
                                      // TODO: Add a "withBirthday" variable.
                                      """
                                              {
                                                "id": 1,
                                                "withBirthday": "true"
                                              }"""));
    }

    /**
     * GraphQL allows you to add type modifiers to queries to perform server side checks.
     * It also supports successful query execution with partial results when errors do occur.
     * Find a non-null field on the Piece type in the composers schema
     * and add it to the query in "src/test/resources/queries/get-composers-compositions.graphql".
     * Notice how the data and errors are handled in the response when querying for a null value of a non-null field.
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/schema/#lists-and-non-null">GraphQL Non-Null</a></li>
     * <li><a href="https://graphql.org/learn/schema/#scalar-types">GraphQL Scalar Types</a></li>
     * </ul>
     */
    @Test
    public void queryComposersCompositionsWithNullKey() throws IOException
    {
        assertEquals("""
                             {
                               "errors" : [
                                 {
                                   "message" : "The field at path '/composers[0]/compositions[0]/key' was declared as a non null type, but the code involved in retrieving data has wrongly returned a null value.  The graphql specification requires that the parent field be set to null, or if that is non nullable that it bubble up null to its parent and so on. The non-nullable type is 'Key' within parent type 'Piece'",
                                   "path" : [
                                     "composers",
                                     0,
                                     "compositions",
                                     0,
                                     "key"
                                   ],
                                   "extensions" : {
                                     "classification" : "NullValueInNonNullableField"
                                   }
                                 }
                               ],
                               "data" : {
                                 "composers" : [
                                   {
                                     "firstName" : "John",
                                     "lastName" : "Cage",
                                     "compositions" : [
                                       null,
                                       {
                                         "__typename" : "Piece",
                                         "title" : "Organ² / ASLSP",
                                         "key" : "Bb"
                                       }
                                     ]
                                   }
                                 ]
                               }
                             }""",
                     executeOperation("/queries/get-composers-compositions.graphql",
                                      """
                                              {
                                                "lastName": "Cage"
                                              }"""));
    }

    /**
     * GraphQL schemas may have interface types. Clients can use inline fragments to query fields on interface subtypes.
     * Add the missing inline fragment, as well as the appropriate arguments and variables,
     * in "src/test/resources/queries/get-composers-compositions.graphql"
     * <p>
     *
     * @see <ul>
     * <li><a href="https://graphql.org/learn/schema/#interfaces">GraphQL Interfaces</a></li>
     * <li><a href="https://graphql.org/learn/schema/#union-types">GraphQL Union Types</a></li>
     * <li><a href="https://graphql.org/learn/queries/#inline-fragments">GraphQL Inline Fragments</a></li>
     * </ul>
     */
    @Test
    public void queryComposerCompositionTypes() throws IOException
    {
        assertEquals("""
                             {
                               "data" : {
                                 "composers" : [
                                   {
                                     "firstName" : "Sergei",
                                     "lastName" : "Rachmaninoff",
                                     "compositions" : [
                                       {
                                         "__typename" : "Concerto",
                                         "title" : "Piano Concerto No. 2",
                                         "key" : "Cm",
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
                     executeOperation("/queries/get-composers-compositions.graphql",
                                      // TODO: Add a value for the "subtype" variable to fetch Rachmaninoff's concertos.
                                      """
                                              {
                                                "lastName": "Rachmaninoff",
                                                "subtype": "Concerto"
                                              }"""));

        assertEquals("""
                             {
                               "data" : {
                                 "composers" : [
                                   {
                                     "firstName" : "Clara",
                                     "lastName" : "Schumann",
                                     "compositions" : [
                                       {
                                         "__typename" : "Song",
                                         "title" : "Liebesfrühling",
                                         "key" : "C#",
                                         "lyrics" : "How often has spring returned\\nFor the dead and desolate world!\\nHow often was spring greeted on all sides\\nBy happy songs in wood and field!\\n\\nHow often has spring returned!\\nBut no spring burgeoned for me:\\nThe songs of my heart are silent,\\nFor spring can only be brought by you."
                                       }
                                     ]
                                   }
                                 ]
                               }
                             }""",
                     executeOperation("/queries/get-composers-compositions.graphql",
                                      // TODO: Add a value for the "subtype" variable to fetch Schumann's songs.
                                      """
                                              {
                                                "lastName": "Schumann",
                                                "subtype": "Song"
                                              }"""));
    }
}
