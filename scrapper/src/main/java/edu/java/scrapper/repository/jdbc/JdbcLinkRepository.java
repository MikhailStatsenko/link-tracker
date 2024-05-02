package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.LinkRepository;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private static final String URL_KEY = "url";
    private static final String CHAT_ID_KEY = "chatId";
    private static final String LINK_ID_KEY = "linkId";
    private static final String LAST_CHECK_TIME_KEY = "lastCheckTime";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Link> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM link",
            new BeanPropertyRowMapper<>(Link.class)
        );
    }

    public List<Link> findAllByChatId(long chatId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(CHAT_ID_KEY, chatId);

        return jdbcTemplate.query(
            "SELECT l.* FROM link l JOIN chat_link cl "
                + "ON l.id = cl.link_id JOIN chat c ON c.id = cl.chat_id WHERE c.id = :chatId",
            paramMap,
            new BeanPropertyRowMapper<>(Link.class)
        );
    }

    public List<Link> findAllOutdatedLinks(long interval) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("interval", interval);

        return jdbcTemplate.query(
            "SELECT * FROM Link WHERE EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - last_check_time)) >= :interval",
            paramMap,
            new BeanPropertyRowMapper<>(Link.class)
        );
    }

    public Optional<Link> findByUrl(String url) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(URL_KEY, url);

        return jdbcTemplate.query(
            "SELECT * FROM link WHERE url = :url",
            paramMap,
            new BeanPropertyRowMapper<>(Link.class)
        ).stream().findAny();
    }

    public Optional<Link> findByChatIdAndUrl(long chatId, String url) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(CHAT_ID_KEY, chatId);
        paramMap.put(URL_KEY, url);

        return jdbcTemplate.query(
            "SELECT l.* FROM link l JOIN chat_link cl ON cl.chat_id = :chatId AND cl.link_id = l.id WHERE l.url = :url",
            paramMap,
            new BeanPropertyRowMapper<>(Link.class)
        ).stream().findAny();
    }

    @Transactional
    public Link save(long chatId, Link link) {
        String url = link.getUrl().toString();
        Optional<Link> savedLink = findByUrl(url);

        if (savedLink.isEmpty()) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(URL_KEY, url);
            paramMap.put(LAST_CHECK_TIME_KEY, link.getLastCheckTime());

            jdbcTemplate.update(
                "INSERT INTO link (url, last_check_time) VALUES (:url, :lastCheckTime)",
                paramMap
            );
            savedLink = findByUrl(url);
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(CHAT_ID_KEY, chatId);
        paramMap.put(LINK_ID_KEY, savedLink.get().getId());

        jdbcTemplate.update(
            "INSERT INTO chat_link (chat_id, link_id) VALUES (:chatId, :linkId)",
            paramMap
        );
        return savedLink.get();
    }

    @Transactional
    public void delete(long chatId, int linkId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(CHAT_ID_KEY, chatId);
        paramMap.put(LINK_ID_KEY, linkId);

        jdbcTemplate.update(
            "DELETE FROM chat_link WHERE chat_id = :chatId AND link_id = :linkId",
            paramMap
        );

        List<Long> chatIdsWithThisLink = jdbcTemplate.queryForList(
            "SELECT chat_id FROM chat_link WHERE link_id = :linkId",
            paramMap,
            Long.class
        );
        if (chatIdsWithThisLink.isEmpty()) {
            jdbcTemplate.update(
                "DELETE FROM link WHERE id = :linkId",
                paramMap
            );
        }
    }

    public void setLastCheckTime(Link link, OffsetDateTime lastCheckTime) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(LAST_CHECK_TIME_KEY, lastCheckTime);
        paramMap.put("id", link.getId());

        jdbcTemplate.update(
            "UPDATE link SET last_check_time = :lastCheckTime WHERE id = :id",
            paramMap
        );
    }
}
