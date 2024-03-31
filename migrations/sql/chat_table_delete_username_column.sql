--liquibase formatted sql

--changeset MikhailStatsenko:4
ALTER TABLE chat DROP COLUMN username;
