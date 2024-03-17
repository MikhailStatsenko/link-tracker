package edu.java.scrapper.repository;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class JdbcChatRepositoryTest extends IntegrationTest {
    private static JdbcChatRepository jdbcChatRepository;

    private static Chat chat1;
    private static Chat chat2;
    private static Chat chat3;

    @BeforeAll
    static void setUp() {
        jdbcChatRepository = new JdbcChatRepository(jdbcTemplate);

        chat1 = new Chat(1L, new ArrayList<>());
        chat2 = new Chat(2L, new ArrayList<>());
        chat3 = new Chat(3L, new ArrayList<>());
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("DELETE FROM chat");
        jdbcTemplate.execute("DELETE FROM link");
        jdbcTemplate.execute("DELETE FROM chat_link");
    }

    @Test
    public void testFindAll() {
        jdbcChatRepository.save(chat1);
        jdbcChatRepository.save(chat2);
        jdbcChatRepository.save(chat3);

        List<Chat> chats = jdbcChatRepository.findAll();

        List<Long> actualIdsList = chats.stream().map(Chat::getId).toList();

        assertThat(chats.size()).isEqualTo(3);
        assertThat(actualIdsList)
            .containsExactlyInAnyOrderElementsOf(
                List.of(chat1.getId(), chat2.getId(), chat3.getId())
            );
    }

    @Test
    public void testFindChatIdsByLinkId() {
        JdbcLinkRepository linkRepository = new JdbcLinkRepository(jdbcTemplate);
        var link = new Link(URI.create("https://github.com/user/example"));

        jdbcChatRepository.save(chat1);
        linkRepository.save(chat1.getId(), link);

        Optional<Link> foundLink = linkRepository.findByUrl(link.getUrl().toString());
        List<Long> chatIds = jdbcChatRepository.findAllChatIdsByLinkId(foundLink.get().getId());

        assertThat(chatIds).containsOnly(chat1.getId());
    }

    @Test
    public void testSave() {
        jdbcChatRepository.save(chat1);

        Optional<Chat> actualChat = jdbcChatRepository.findById(chat1.getId());
        System.out.println(actualChat);
        assertThat(actualChat).isPresent();
        assertThat(actualChat.get().getId()).isEqualTo(chat1.getId());
    }

    @Test
    public void testFindById() {
        jdbcChatRepository.save(chat1);

        Optional<Chat> actualChat = jdbcChatRepository.findById(chat1.getId());

        assertThat(actualChat.get().getId()).isEqualTo(chat1.getId());
    }

    @Test
    public void testFindByIdWhenChatNotPresent() {
        long testId = 12345L;

        Optional<Chat> foundChat = jdbcChatRepository.findById(testId);
        assertThat(foundChat).isEmpty();
    }

    @Test
    public void testDelete() {
        jdbcChatRepository.save(chat1);

        jdbcChatRepository.delete(chat1.getId());

        Optional<Chat> foundChat = jdbcChatRepository.findById(chat1.getId());
        assertThat(foundChat).isEmpty();
    }
}
