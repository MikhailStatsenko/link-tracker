package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.model.Chat;
import edu.java.scrapper.repository.ChatRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatRepository implements ChatRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Chat> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM chat",
            new BeanPropertyRowMapper<>(Chat.class)
        );
    }

    public List<Long> findAllChatIdsByLinkId(long linkId) {
        return jdbcTemplate.queryForList(
            "SELECT chat_id FROM chat_link WHERE link_id = ?",
            Long.class,
            linkId
        );
    }

    public Optional<Chat> findById(long chatId) {
        return jdbcTemplate.query(
            "SELECT * FROM chat WHERE id = ?",
                new BeanPropertyRowMapper<>(Chat.class),
                chatId
            ).stream()
            .findAny();
    }

    public void save(Chat chat) {
        jdbcTemplate.update(
            "INSERT INTO chat (id) VALUES (?)",
            chat.getId()
        );
    }

    public int delete(long chatId) {
        return jdbcTemplate.update(
            "DELETE FROM chat WHERE id = ?",
            chatId
        );
    }
}
