package edu.java.scrapper.repository.jooq;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class JooqChatRepositoryTest extends IntegrationTest {
    @Autowired
    DSLContext dsl;
    private JooqChatRepository jooqChatRepository;
    private JooqLinkRepository jooqLinkRepository;

    private static final Chat chat1 = new Chat(1L, new ArrayList<>());
    private static final Chat chat2 = new Chat(2L, new ArrayList<>());
    private static final Chat chat3 = new Chat(3L, new ArrayList<>());

    @BeforeEach
    void setUp() {
        jooqChatRepository = new JooqChatRepository(dsl);
        jooqLinkRepository = new JooqLinkRepository(dsl);
    }

    @Test
    public void testFindAll() {
        jooqChatRepository.save(chat1);
        jooqChatRepository.save(chat2);
        jooqChatRepository.save(chat3);

        List<Chat> chats = jooqChatRepository.findAll();

        List<Long> actualIdsList = chats.stream().map(Chat::getId).toList();

        assertThat(chats.size()).isEqualTo(3);
        assertThat(actualIdsList)
            .containsExactlyInAnyOrderElementsOf(
                List.of(chat1.getId(), chat2.getId(), chat3.getId())
            );
    }

    @Test
    public void testFindChatIdsByLinkId() {
        var link = new Link(URI.create("https://github.com/user/example"));

        jooqChatRepository.save(chat1);
        jooqLinkRepository.save(chat1.getId(), link);

        Optional<Link> foundLink = jooqLinkRepository.findByUrl(link.getUrl().toString());
        System.out.println(jooqChatRepository.findAll());
        List<Long> chatIds = jooqChatRepository.findAllChatIdsByLinkId(foundLink.get().getId());

        assertThat(chatIds).containsOnly(chat1.getId());
    }

    @Test
    public void testSave() {
        jooqChatRepository.save(chat1);

        Optional<Chat> actualChat = jooqChatRepository.findById(chat1.getId());
        System.out.println(actualChat);
        assertThat(actualChat).isPresent();
        assertThat(actualChat.get().getId()).isEqualTo(chat1.getId());
    }

    @Test
    public void testFindById() {
        jooqChatRepository.save(chat1);

        Optional<Chat> actualChat = jooqChatRepository.findById(chat1.getId());

        assertThat(actualChat.get().getId()).isEqualTo(chat1.getId());
    }

    @Test
    public void testFindByIdWhenChatNotPresent() {
        long testId = 12345L;

        Optional<Chat> foundChat = jooqChatRepository.findById(testId);
        assertThat(foundChat).isEmpty();
    }

    @Test
    public void testDelete() {
        jooqChatRepository.save(chat1);

        jooqChatRepository.delete(chat1.getId());

        Optional<Chat> foundChat = jooqChatRepository.findById(chat1.getId());
        assertThat(foundChat).isEmpty();
    }
}
