package edu.java.scrapper.scrapper;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.assertj.core.api.Assertions.assertThat;

public class LiquibaseIntegrationTest extends IntegrationTest {
    private static JdbcTemplate jdbcTemplate;

    private final long chatId1 = 123L;
    private final long chatId2 = 456L;

    private final String username1 = "username1";
    private final String username2 = "username2";

    private final String link1 = "https://github.com/sanyarnd/tinkoff-java-course-2023/";
    private final String link2 = "https://stackoverflow.com/search?q=unsupported%20link";

    @BeforeAll
    public static void setup() {
        jdbcTemplate = new JdbcTemplate(
            DataSourceBuilder.create()
                .url(POSTGRES.getJdbcUrl())
                .username(POSTGRES.getUsername())
                .password(POSTGRES.getPassword())
                .build()
        );
    }

    @BeforeEach
    public void beforeEach() {
        jdbcTemplate.update("DELETE FROM chat");
        jdbcTemplate.update("DELETE FROM link");
        jdbcTemplate.update("DELETE FROM chat_link");
    }

   @Test
    public void testConnectionProperties() {
        assertThat(POSTGRES.isRunning()).isTrue();
        assertThat(POSTGRES.getUsername()).isEqualTo("postgres");
        assertThat(POSTGRES.getPassword()).isEqualTo("postgres");
        assertThat(POSTGRES.getDatabaseName()).isEqualTo("scrapper");
   }

   @Test
   public void testScrapperDBChatTable() {
       String insertSql = "INSERT INTO chat (id, username) VALUES (?, ?)";
       jdbcTemplate.update(insertSql, chatId1, username1);
       jdbcTemplate.update(insertSql, chatId2, username2);

       String selectSql = "SELECT * FROM chat";
       List<String> actual = jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getString("username"));

       assertThat(actual.size()).isEqualTo(2);
       assertThat(actual).containsExactlyInAnyOrderElementsOf(List.of(username1, username2));
   }

    @Test
    public void testScrapperDBLinkTable() {
        String insertSql = "INSERT INTO link (url) VALUES (?)";
        jdbcTemplate.update(insertSql, link1);
        jdbcTemplate.update(insertSql, link2);

        String selectSql = "SELECT * FROM link";
        List<String> actual = jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getString("url"));

        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(List.of(link1, link2));
    }

    @Test
    public void testScrapperDBChatLinkTable() {
        String insertChatSql = "INSERT INTO chat (id, username) VALUES (?, ?)";
        jdbcTemplate.update(insertChatSql, chatId1, username1);
        jdbcTemplate.update(insertChatSql, chatId2, username2);

        String insertLinkSql = "INSERT INTO link (url) VALUES (?)";
        jdbcTemplate.update(insertLinkSql, link1);
        jdbcTemplate.update(insertLinkSql, link2);

        String insertChatLinksSql = "INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)";
        jdbcTemplate.update(insertChatLinksSql, chatId1, 3);
        jdbcTemplate.update(insertChatLinksSql, chatId2, 3);

        String selectSql = "SELECT COUNT(*) FROM chat_link where link_id = 3";
        Integer actual = jdbcTemplate.queryForObject(selectSql, Integer.class);

        assertThat(actual).isEqualTo(2);
    }
}
