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

CREATE TABLE IF NOT EXISTS `composer`
(
    `id`                    INTEGER PRIMARY KEY AUTO_INCREMENT,
    `first_name`            VARCHAR(250),
    `last_name`             VARCHAR(250) NOT NULL,
    `date_of_birth`         DATE         NOT NULL,
    `location_id`           INTEGER,
    `period`                INTEGER,
    `primary_instrument_id` INTEGER
);

CREATE TABLE IF NOT EXISTS `instrument`
(
    `id`   INTEGER PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(250),
    `type` INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS `location`
(
    `id`      INTEGER PRIMARY KEY AUTO_INCREMENT,
    `city`    VARCHAR(250),
    `country` VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS `composition`
(
    `id`                 INTEGER PRIMARY KEY AUTO_INCREMENT,
    `subtype`            VARCHAR(250) NOT NULL,
    `composer_id`        INTEGER      NOT NULL,
    `title`              VARCHAR(250),
    `key_center`                VARCHAR(250),
    `solo_instrument_id` INTEGER,
    `lyrics`             VARCHAR(1000)
);

ALTER TABLE `composer`
    ADD FOREIGN KEY (`location_id`) REFERENCES `location` (`id`);

ALTER TABLE `composer`
    ADD CONSTRAINT composer UNIQUE (`first_name`, `last_name`);

ALTER TABLE `composer`
    ADD FOREIGN KEY (`primary_instrument_id`) REFERENCES `instrument` (`id`);

ALTER TABLE `composition`
    ADD FOREIGN KEY (`solo_instrument_id`) REFERENCES `instrument` (`id`);

ALTER TABLE `composition`
    ADD FOREIGN KEY (`composer_id`) REFERENCES `composer` (`id`);

ALTER TABLE `composition`
    ADD CONSTRAINT composition UNIQUE (`composer_id`, `title`);

ALTER TABLE `location`
    ADD CONSTRAINT location UNIQUE (`city`, `country`);

ALTER TABLE `instrument`
    ADD CONSTRAINT instrument UNIQUE (`name`, `type`);