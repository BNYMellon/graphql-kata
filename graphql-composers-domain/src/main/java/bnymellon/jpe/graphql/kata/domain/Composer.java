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

package bnymellon.jpe.graphql.kata.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.impl.factory.Lists;

public record Composer(int id,
                       String firstName,
                       String lastName,
                       LocalDate dateOfBirth,
                       Location location,
                       Instrument primaryInstrument,
                       Period period,
                       List<Composition> compositions)
{
    public static Composer fromInput(int id, Map<String, Object> input)
    {

        return new Composer(id,
                            Optional.ofNullable(input.get("firstName")).map(Object::toString).orElse(null),
                            Optional.ofNullable(input.get("lastName")).map(Object::toString).orElse(null),
                            ((LocalDate) input.get("dateOfBirth")),
                            Location.fromInput((Map<String, String>) input.get("location")),
                            Instrument.fromInput((Map<String, String>) input.get("primaryInstrument")),
                            Optional.ofNullable(input.get("period")).map(Object::toString)
                                    .map(Period::valueOf).orElse(null),
                            List.of());
    }

    public List<Composition> compositions(Predicate<Composition> predicate)
    {
        return Lists.adapt(this.compositions).select(predicate);
    }
}
