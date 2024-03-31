--liquibase formatted sql

--changeset MikhailStatsenko:5
ALTER TABLE link ALTER COLUMN last_check_time SET DATA TYPE TIMESTAMP WITH TIME ZONE;
