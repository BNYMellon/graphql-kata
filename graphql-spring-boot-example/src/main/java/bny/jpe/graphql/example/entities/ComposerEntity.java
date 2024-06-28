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

package bny.jpe.graphql.example.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import bny.jpe.graphql.kata.domain.Composer;
import bny.jpe.graphql.kata.domain.Period;

@Entity
@Table(name = "composer")
public class ComposerEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String firstName;
    public String lastName;
    public LocalDate dateOfBirth;
    @OneToOne
    public LocationEntity location;
    @OneToOne
    public InstrumentEntity primaryInstrument;
    public Period period;
    @OneToMany(mappedBy = "composer", fetch = FetchType.EAGER)
    public List<CompositionEntity> compositions;

    public Composer toRecord()
    {
        return new Composer(this.id,
                            this.firstName,
                            this.lastName,
                            this.dateOfBirth,
                            Optional.ofNullable(this.location).map(LocationEntity::toRecord).orElse(null),
                            Optional.ofNullable(this.primaryInstrument).map(InstrumentEntity::toRecord).orElse(null),
                            this.period,
                            this.compositions.stream().map(CompositionEntity::toRecord).collect(Collectors.toList()));
    }
}
