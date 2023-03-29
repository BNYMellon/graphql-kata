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

INSERT INTO `instrument` (`name`, `type`)
VALUES ('Organ', 1),
       ('Piano', 1),
       ('Harpsichord', 1),
       ('Clarinet', 2);

INSERT INTO `location` (`city`, `country`)
VALUES ('Leipzig', 'Germany'),
       ('Vienna', 'Austria'),
       ('Frankfurt', 'Germany'),
       ('Rome', 'Italy'),
       ('Moscow', 'Russia'),
       ('Paris', 'France'),
       ('New York', 'United States');

INSERT INTO `composer` (`first_name`, `last_name`, `date_of_birth`, `location_id`, `primary_instrument_id`, `period`)
VALUES ('Johann', 'Bach',
        '1685-03-31',
        (SELECT `id` FROM `location` WHERE `city` = 'Leipzig'),
        (SELECT `id` FROM `instrument` WHERE `name` = 'Organ'),
        1),
       ('Ludwig', 'Beethoven',
        '1770-03-26',
        (SELECT `id` FROM `location` WHERE `city` = 'Vienna'),
        (SELECT `id` FROM `instrument` WHERE `name` = 'Piano'),
        2),
       ('Wolfgang', 'Mozart',
        '1756-01-27',
        (SELECT `id` FROM `location` WHERE `city` = 'Vienna'),
        (SELECT `id` FROM `instrument` WHERE `name` = 'Harpsichord'),
        2),
       ('Clara', 'Schumann',
        '1819-09-13',
        (SELECT `id` FROM `location` WHERE `city` = 'Frankfurt'),
        (SELECT `id` FROM `instrument` WHERE `name` = 'Piano'),
        3),
       ('Giovanni', 'Palestrina',
        '1594-02-02',
        (SELECT `id` FROM `location` WHERE `city` = 'Rome'),
        (SELECT `id` FROM `instrument` WHERE `name` = 'Organ'),
        0),
       ('Sergei', 'Rachmaninoff',
        '1873-04-01',
        (SELECT `id` FROM `location` WHERE `city` = 'Moscow'),
        (SELECT `id` FROM `instrument` WHERE `name` = 'Piano'),
        3),
       ('Maurice', 'Ravel',
        '1875-03-07',
        (SELECT `id` FROM `location` WHERE `city` = 'Paris'),
        (SELECT `id` FROM `instrument` WHERE `name` = 'Piano'),
        4),
       ('John', 'Cage',
        '1912-09-05',
        (SELECT `id` FROM `location` WHERE `city` = 'New York'),
        (SELECT `id` FROM `instrument` WHERE `name` = 'Piano'),
        5);

INSERT INTO composition(`subtype`, `composer_id`, `title`, `key_center`, `solo_instrument_id`, `lyrics`)
VALUES ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Bach'), 'Toccata and Fugue in D minor', 'Dm', null, null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Bach'), 'Mass in B minor', 'Bm', null, null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Bach'), 'Cello Suite No. 1', 'G', null, null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Beethoven'), 'Symphony No. 5', 'Cm', null, null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Beethoven'), 'Moonlight Sonata', 'C#m', null, null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Mozart'), 'Eine kleine Nachtmusik', 'G', null, null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Mozart'), 'Requiem', 'Dm', null, null),
       ('Concerto', (SELECT `id` FROM `composer` WHERE `last_name` = 'Mozart'), 'Clarinet Concerto in A Major', 'A',
        (SELECT `id` FROM `instrument` WHERE `name` = 'Clarinet'), null),
       ('Concerto', (SELECT `id` FROM `composer` WHERE `last_name` = 'Schumann'), 'Piano Concerto in A minor', 'Am',
        (SELECT `id` FROM `instrument` WHERE `name` = 'Piano'), null),
       ('Song', (SELECT `id` FROM `composer` WHERE `last_name` = 'Schumann'), 'Liebesfrühling', 'C#', null,
        'How often has spring returned
For the dead and desolate world!
How often was spring greeted on all sides
By happy songs in wood and field!

How often has spring returned!
But no spring burgeoned for me:
The songs of my heart are silent,
For spring can only be brought by you.'),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Palestrina'), 'Missa Papae Marcelli', 'C', null, null),
       ('Concerto', (SELECT `id` FROM `composer` WHERE `last_name` = 'Rachmaninoff'), 'Piano Concerto No. 2', 'Cm',
        (SELECT `id` FROM `instrument` WHERE `name` = 'Piano'), null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Rachmaninoff'), 'Symphony No. 2', 'Em', null, null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Ravel'), 'Bolero', 'C', null, null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Ravel'), 'La Valse', 'D', null, null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Cage'), '4:33', null, null, null),
       ('Piece', (SELECT `id` FROM `composer` WHERE `last_name` = 'Cage'), 'Organ² / ASLSP', 'Bb', null, null);