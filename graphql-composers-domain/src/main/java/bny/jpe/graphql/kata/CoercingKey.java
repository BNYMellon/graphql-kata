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

package bny.jpe.graphql.kata;

import java.util.Objects;
import java.util.regex.Pattern;

import graphql.GraphqlErrorException;
import graphql.com.google.common.base.Function;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

public class CoercingKey
        implements Coercing<String, String>
{
    private static final Pattern KEY_PATTERN = Pattern.compile("[A-G](b|#)?m?");

    @Override
    public String serialize(Object dataFetcherResult) throws CoercingSerializeException
    {
        return validateKey(dataFetcherResult, CoercingSerializeException::new);
    }

    @Override
    public String parseValue(Object input) throws CoercingParseValueException
    {
        return validateKey(input, CoercingParseValueException::new);
    }

    @Override
    public String parseLiteral(Object input) throws CoercingParseLiteralException
    {
        return validateKey(((StringValue) input).getValue(), CoercingParseLiteralException::new);
    }

    private <T extends GraphqlErrorException> String validateKey(Object dataFetcherResult,
                                                                 Function<String, T> error) throws T
    {
        if (KEY_PATTERN.asPredicate().test(String.valueOf(dataFetcherResult)))
        {
            return String.valueOf(dataFetcherResult);
        }
        else
        {
            throw Objects.requireNonNull(error.apply("Key must match the patter: " + KEY_PATTERN));
        }
    }
}
