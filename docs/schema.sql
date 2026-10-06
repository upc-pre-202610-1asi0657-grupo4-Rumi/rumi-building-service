-- Schema of buildingsdb (PostgreSQL), the database of rumi-building-service.
-- The default profile runs Hibernate with ddl-auto=validate, so these tables must exist.
-- Create the database first:  CREATE DATABASE buildingsdb;

CREATE TABLE buildings (
    id                    uuid         PRIMARY KEY,
    name                  varchar(100) NOT NULL,
    address               varchar(200) NOT NULL,
    floors                integer      NOT NULL,
    construction_year     integer      NOT NULL,
    units                 integer      NOT NULL,
    status                varchar(30)  NOT NULL CHECK (status IN ('PENDING_SENSORS', 'ACTIVE')),
    administrator_user_id uuid         NOT NULL
);

CREATE TABLE sensors (
    id          uuid        PRIMARY KEY,
    building_id uuid        NOT NULL REFERENCES buildings (id),
    zone        varchar(50) NOT NULL,
    type        varchar(20) NOT NULL CHECK (type IN ('ACCELEROMETER', 'INCLINOMETER')),
    status      varchar(20) NOT NULL CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE'))
);

CREATE TABLE resident_invitations (
    id          uuid        PRIMARY KEY,
    building_id uuid        NOT NULL REFERENCES buildings (id),
    code        varchar(20) NOT NULL UNIQUE,
    status      varchar(20) NOT NULL CHECK (status IN ('PENDING', 'USED', 'EXPIRED')),
    created_at  timestamp   NOT NULL
);

CREATE INDEX sensors_building_id_idx ON sensors (building_id);
CREATE INDEX resident_invitations_building_id_idx ON resident_invitations (building_id);
