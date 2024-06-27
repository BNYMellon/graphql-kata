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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

public class CoercingDate
        implements Coercing
{
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

    @Override
    public Object serialize(Object dataFetcherResult) throws CoercingSerializeException
    {
        return FORMATTER.format(((LocalDate) dataFetcherResult));
    }

    @Override
    public LocalDate parseValue(Object input) throws CoercingParseValueException
    {
        return parseDate(input);
    }

    @Override
    public LocalDate parseLiteral(Object input) throws CoercingParseLiteralException
    {
        return parseDate(input);
    }

    private LocalDate parseDate(Object dataFetcherResult)
    {
        if (dataFetcherResult instanceof StringValue)
        {
            return LocalDate.from(FORMATTER.parse(((StringValue) dataFetcherResult).getValue()));
        }
        else if (dataFetcherResult instanceof String)
        {
            return LocalDate.from(FORMATTER.parse((CharSequence) dataFetcherResult));
        }
        return null;
    }
}
