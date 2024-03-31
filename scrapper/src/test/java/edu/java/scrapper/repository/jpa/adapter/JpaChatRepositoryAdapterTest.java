package edu.java.scrapper.repository.jpa.adapter;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.jooq.JooqChatRepository;
import edu.java.scrapper.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.repository.jpa.JpaChatRepository;
import edu.java.scrapper.repository.jpa.JpaChatRepositoryAdapter;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaLinkRepositoryAdapter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JpaChatRepositoryAdapterTest extends IntegrationTest {
    @Autowired
    private JpaLinkRepository jpaLinkRepository;
    @Autowired
    private JpaChatRepository jpaChatRepository;

    private JpaChatRepositoryAdapter jpaChatRepositoryAdapter;
    private JpaLinkRepositoryAdapter jpaLinkRepositoryAdapter;

    private static final Chat chat1 = new Chat(1L, new ArrayList<>());
    private static final Chat chat2 = new Chat(2L, new ArrayList<>());
    private static final Chat chat3 = new Chat(3L, new ArrayList<>());

    @BeforeEach
    void setUp() {
        jpaChatRepositoryAdapter = new JpaChatRepositoryAdapter(jpaChatRepository);
        jpaLinkRepositoryAdapter = new JpaLinkRepositoryAdapter(jpaLinkRepository);
    }

    @Test
    public void testFindAll() {
        jpaChatRepositoryAdapter.save(chat1);
        jpaChatRepositoryAdapter.save(chat2);
        jpaChatRepositoryAdapter.save(chat3);

        List<Chat> chats = jpaChatRepositoryAdapter.findAll();

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

        jpaChatRepositoryAdapter.save(chat1);
        jpaLinkRepositoryAdapter.save(chat1.getId(), link);

        Optional<Link> foundLink = jpaLinkRepositoryAdapter.findByUrl(link.getUrl().toString());
        System.out.println(jpaChatRepositoryAdapter.findAll());
        List<Long> chatIds = jpaChatRepositoryAdapter.findAllChatIdsByLinkId(foundLink.get().getId());

        assertThat(chatIds).containsOnly(chat1.getId());
    }

    @Test
    public void testSave() {
        jpaChatRepositoryAdapter.save(chat1);

        Optional<Chat> actualChat = jpaChatRepositoryAdapter.findById(chat1.getId());
        System.out.println(actualChat);
        assertThat(actualChat).isPresent();
        assertThat(actualChat.get().getId()).isEqualTo(chat1.getId());
    }

    @Test
    public void testFindById() {
        jpaChatRepositoryAdapter.save(chat1);

        Optional<Chat> actualChat = jpaChatRepositoryAdapter.findById(chat1.getId());

        assertThat(actualChat.get().getId()).isEqualTo(chat1.getId());
    }

    @Test
    public void testFindByIdWhenChatNotPresent() {
        long testId = 12345L;

        Optional<Chat> foundChat = jpaChatRepositoryAdapter.findById(testId);
        assertThat(foundChat).isEmpty();
    }

    @Test
    public void testDelete() {
        jpaChatRepositoryAdapter.save(chat1);

        jpaChatRepositoryAdapter.delete(chat1.getId());

        Optional<Chat> foundChat = jpaChatRepositoryAdapter.findById(chat1.getId());
        assertThat(foundChat).isEmpty();
    }

}
