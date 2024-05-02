package edu.java.scrapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class MigrationsIntegrationTest extends IntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

   @Test
    public void testConnectionProperties() {
        assertThat(POSTGRES.isRunning()).isTrue();
        assertThat(POSTGRES.getUsername()).isEqualTo("postgres");
        assertThat(POSTGRES.getPassword()).isEqualTo("postgres");
        assertThat(POSTGRES.getDatabaseName()).isEqualTo("scrapper");
   }

   @Test
   public void testScrapperDBChatTable() {
       long chatId1 = 1L, chatId2 = 2L;

       String insertSql = "INSERT INTO chat (id) VALUES (?)";
       jdbcTemplate.update(insertSql, chatId1);
       jdbcTemplate.update(insertSql, chatId2);

       String selectSql = "SELECT * FROM chat";
       List<String> actual = jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getString("id"));

       assertThat(actual.size()).isEqualTo(2);
       assertThat(actual).containsExactlyInAnyOrderElementsOf(List.of(String.valueOf(chatId1), String.valueOf(chatId2)));
   }

    @Test
    public void testScrapperDBLinkTable() {
        String link1 = "https://github.com/sanyarnd/tinkoff-java-course-2023/";
        String link2 = "https://stackoverflow.com/search?q=unsupported%20link";

        String insertSql = "INSERT INTO link (url) VALUES (?)";
        jdbcTemplate.update(insertSql, link1);
        jdbcTemplate.update(insertSql, link2);

        String selectSql = "SELECT * FROM link";
        List<String> actual = jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getString("url"));

        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(List.of(link1, link2));
    }
}
