--liquibase formatted sql

--changeset MikhailStatsenko:3
CREATE TABLE IF NOT EXISTS chat_link
(
    chat_id BIGINT NOT NULL,
    link_id BIGINT NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chat(id) ON DELETE CASCADE,
    FOREIGN KEY (link_id) REFERENCES link(id) ON DELETE CASCADE,
    PRIMARY KEY (chat_id, link_id)
);
--rollback drop table chat_link;
