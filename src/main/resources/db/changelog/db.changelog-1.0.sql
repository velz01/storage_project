--liquibase formatted sql

--changeset velz12:1
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(64) NOT NULL UNIQUE,
                       password VARCHAR(128)

);