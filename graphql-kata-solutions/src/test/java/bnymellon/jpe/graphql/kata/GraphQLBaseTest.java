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

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import graphql.ExecutionInput;
import graphql.GraphQL;

public abstract class GraphQLBaseTest
{
    protected GraphQL graphQL;
    private ObjectWriter writer;
    private ObjectMapper mapper;

    @BeforeAll
    public void setUp()
    {
        graphQL = GraphQL.newGraphQL(new ComposersSchema().initSchema()).build();
        DefaultIndenter indenter = new DefaultIndenter().withLinefeed("\n");
        mapper = new ObjectMapper().findAndRegisterModules();
        writer = mapper.writer(new DefaultPrettyPrinter().withObjectIndenter(indenter).withArrayIndenter(indenter));
    }

    private String getGqlFile(String path) throws IOException
    {
        return new String(this.getClass().getResourceAsStream(path).readAllBytes());
    }

    protected String executeOperation(String gqlResourcePath) throws IOException
    {
        return getJSONResponse(ExecutionInput.newExecutionInput(getGqlFile(gqlResourcePath)));
    }

    protected String executeOperation(String queryResourcePath, String jsonVariables) throws IOException
    {
        Map<String, Object> vars = mapper.readValue(jsonVariables, new TypeReference<>()
        {
        });
        return getJSONResponse(ExecutionInput.newExecutionInput(getGqlFile(queryResourcePath)).variables(vars));
    }

    private String getJSONResponse(ExecutionInput.Builder input) throws JsonProcessingException
    {
        return writer.writeValueAsString(graphQL.execute(input.build()).toSpecification());
    }
}
