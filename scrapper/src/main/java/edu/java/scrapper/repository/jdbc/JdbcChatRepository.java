package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.model.Chat;
import edu.java.scrapper.repository.ChatRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatRepository implements ChatRepository {
    private static final String CHAT_ID_KEY = "chatId";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Chat> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM chat",
            new BeanPropertyRowMapper<>(Chat.class)
        );
    }

    public List<Long> findAllChatIdsByLinkId(long linkId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("linkId", linkId);
        return jdbcTemplate.queryForList(
            "SELECT chat_id FROM chat_link WHERE link_id = :linkId",
            paramMap,
            Long.class
        );
    }

    public Optional<Chat> findById(long chatId) {
        Map<String, Object> paramMap = Collections.singletonMap(CHAT_ID_KEY, chatId);
        return jdbcTemplate.query(
            "SELECT * FROM chat WHERE id = :chatId",
            paramMap,
            new BeanPropertyRowMapper<>(Chat.class)
        ).stream().findAny();
    }

    public void save(Chat chat) {
        Map<String, Object> paramMap = Collections.singletonMap("id", chat.getId());
        jdbcTemplate.update(
            "INSERT INTO chat (id) VALUES (:id)",
            paramMap
        );
    }

    public int delete(long chatId) {
        Map<String, Object> paramMap = Collections.singletonMap(CHAT_ID_KEY, chatId);
        return jdbcTemplate.update(
            "DELETE FROM chat WHERE id = :chatId",
            paramMap
        );
    }
}
