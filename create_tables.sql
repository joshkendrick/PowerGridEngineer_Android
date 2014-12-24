--------------------------------------------------
--
-- Power Grid Engineer
-- 31Ten Software
--
-- Author: Josh Kendrick
-- Created: 2014-05-02
--
--------------------------------------------------

-- Can sqlite3 power_grid.sqlite to create a database
-- run the below file by .read create_tables.sql

--Android requires a table named 'android_metadata' with a 'locale' column
CREATE TABLE IF NOT EXISTS android_metadata (
    locale TEXT DEFAULT 'en_US'
);

INSERT INTO android_metadata VALUES ('en_US');

CREATE TABLE IF NOT EXISTS region (
    id          INTEGER     PRIMARY KEY AUTOINCREMENT NOT NULL,
    color       TEXT        NOT NULL,
    active      INTEGER     NOT NULL DEFAULT 1
);

INSERT INTO region (color) VALUES ('PURPLE');
INSERT INTO region (color) VALUES ('YELLOW');
INSERT INTO region (color) VALUES ('BROWN');
INSERT INTO region (color) VALUES ('TEAL');
INSERT INTO region (color) VALUES ('RED');
INSERT INTO region (color) VALUES ('GREEN');
INSERT INTO region (color, active) VALUES ('BLUE', 0);

CREATE TABLE IF NOT EXISTS country (
    id          INTEGER     PRIMARY KEY AUTOINCREMENT NOT NULL,
    name        TEXT        NOT NULL,
    active      INTEGER     NOT NULL DEFAULT 1
);

INSERT INTO country (name) VALUES ('UNITED STATES');
INSERT INTO country (name, active) VALUES ('GERMANY', 0);

CREATE TABLE IF NOT EXISTS city (
    id          INTEGER     PRIMARY KEY AUTOINCREMENT NOT NULL,
    name        TEXT        NOT NULL,
    region      INTEGER     NOT NULL REFERENCES region(id) ON DELETE CASCADE ON UPDATE CASCADE,
    country     INTEGER     NOT NULL REFERENCES country(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS path (
    id              INTEGER     PRIMARY KEY AUTOINCREMENT NOT NULL,
    cost            INTEGER     NOT NULL,
    source          INTEGER     NOT NULL REFERENCES city(id) ON DELETE CASCADE ON UPDATE CASCADE,
    destination     INTEGER     NOT NULL REFERENCES city(id) ON DELETE CASCADE ON UPDATE CASCADE
);