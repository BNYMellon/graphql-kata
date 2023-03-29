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

package bnymellon.jpe.graphql.example.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import bnymellon.jpe.graphql.kata.domain.Composition;
import bnymellon.jpe.graphql.kata.domain.Concerto;
import bnymellon.jpe.graphql.kata.domain.Piece;
import bnymellon.jpe.graphql.kata.domain.Song;

@Entity
@Table(name = "composition")
public class CompositionEntity
{
    @Id
    public int id;
    @ManyToOne
    @JoinColumn(name = "composer_id")
    public ComposerEntity composer;
    public String subtype;
    public String title;
    public String keyCenter;
    @OneToOne
    public InstrumentEntity soloInstrument;
    public String lyrics;

    public Composition toRecord()
    {
        return switch (this.subtype)
                {
                    case "Concerto" -> new Concerto(this.title,
                                                    this.keyCenter,
                                                    this.soloInstrument.toRecord());
                    case "Song" -> new Song(this.title, this.keyCenter, this.lyrics);
                    default -> new Piece(this.title, this.keyCenter);
                };
    }
}
