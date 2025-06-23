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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import bny.jpe.graphql.kata.domain.Location;

@Entity
@Table(name = "location")
public class LocationEntity
{
    @Id
    public int id;
    public String city;
    public String country;

    public Location toRecord()
    {
        return new Location(this.city, this.country);
    }
}
