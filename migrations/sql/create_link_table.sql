--liquibase formatted sql

--changeset MikhailStatsenko:2
CREATE TABLE IF NOT EXISTS link
(
    id SERIAL PRIMARY KEY,
    url VARCHAR(255) UNIQUE NOT NULL,
    last_check_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--rollback drop table link;
