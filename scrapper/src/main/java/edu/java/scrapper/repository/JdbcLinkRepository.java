package edu.java.scrapper.repository;

import edu.java.scrapper.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JdbcLinkRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Link> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM link",
            new BeanPropertyRowMapper<>(Link.class)
        );
    }

    public List<Link> findAllByChatId(long chatId) {
        return jdbcTemplate.query(
            "SELECT l.* FROM link l JOIN chat_link cl "
                + "ON l.id = cl.link_id JOIN chat c ON c.id = cl.chat_id WHERE c.id=?",
                new BeanPropertyRowMapper<>(Link.class),
                chatId
            );
    }

    public List<Link> findAllOutdatedLinks(long interval) {
        return jdbcTemplate
            .query(
                "SELECT * FROM Link WHERE EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - last_check_time)) >= ?",
                new BeanPropertyRowMapper<>(Link.class), interval
            );
    }

    public Optional<Link> findByUrl(String url) {
        return jdbcTemplate.query(
            "SELECT * FROM link WHERE url = ?",
                new BeanPropertyRowMapper<>(Link.class),
                url
            ).stream()
            .findAny();
    }

    public Optional<Link> findByChatIdAndUrl(long chatId, String url) {
        return jdbcTemplate.query(
            "SELECT l.* FROM link l JOIN chat_link cl ON cl.chat_id = ? AND cl.link_id = l.id WHERE l.url = ?",
                new BeanPropertyRowMapper<>(Link.class),
                chatId,
                url
            ).stream()
            .findAny();
    }

    @Transactional
    public Link save(long chatId, Link link) {
        String url = link.getUrl().toString();
        Optional<Link> savedLink = findByUrl(url);

        if (savedLink.isEmpty()) {
            jdbcTemplate.update(
                "INSERT INTO link (url, last_check_time) VALUES (?, ?)",
                url, link.getLastCheckTime()
            );
            savedLink = findByUrl(url);
        }

        jdbcTemplate.update(
            "INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)",
            chatId,
            savedLink.get().getId()
        );
        return savedLink.get();
    }

    @Transactional
    public void delete(long chatId, long linkId) {
        jdbcTemplate.update("DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?", chatId, linkId);

        List<Long> chatIdsWithThisLink =
            jdbcTemplate.queryForList("SELECT chat_id FROM chat_link WHERE link_id = ?", Long.class, linkId);
        if (chatIdsWithThisLink.isEmpty()) {
            jdbcTemplate.update("DELETE FROM link WHERE id = ?", linkId);
        }
    }

    public void setLastCheckTime(Link link, OffsetDateTime lastCheckTime) {
        jdbcTemplate.update(
            "UPDATE link SET last_check_time = ? WHERE id = ?",
            lastCheckTime,
            link.getId()
        );
    }
}
