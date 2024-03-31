--liquibase formatted sql

--changeset MikhailStatsenko:6
ALTER TABLE chat_link ALTER COLUMN link_id SET DATA TYPE INTEGER;
