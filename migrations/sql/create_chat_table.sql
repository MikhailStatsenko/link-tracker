--liquibase formatted sql

--changeset MikhailStatsenko:1
CREATE TABLE IF NOT EXISTS chat
(
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL
);
--rollback drop table chat;
