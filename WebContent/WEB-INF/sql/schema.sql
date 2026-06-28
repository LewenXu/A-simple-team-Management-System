CREATE TABLE teams (
    id INTEGER NOT NULL PRIMARY KEY,
    local_name VARCHAR(80) NOT NULL,
    team_name VARCHAR(80) NOT NULL
);

CREATE TABLE managers (
    id INTEGER NOT NULL PRIMARY KEY,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    team_id INTEGER
);

CREATE TABLE players (
    id INTEGER NOT NULL PRIMARY KEY,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    playing_position VARCHAR(30) NOT NULL,
    team_id INTEGER
);

CREATE TABLE active_slots (
    team_id INTEGER NOT NULL,
    slot_number INTEGER NOT NULL,
    player_id INTEGER,
    PRIMARY KEY (team_id, slot_number)
);
