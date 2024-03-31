package edu.java.scrapper.repository.jpa.adapter;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.jpa.JpaChatRepository;
import edu.java.scrapper.repository.jpa.JpaChatRepositoryAdapter;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaLinkRepositoryAdapter;
import java.net.URI;
import java.time.OffsetDateTime;
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
class JpaLinkRepositoryAdapterTest extends IntegrationTest {
    @Autowired
    private JpaLinkRepository jpaLinkRepository;
    @Autowired
    private JpaChatRepository jpaChatRepository;

    private JpaChatRepositoryAdapter jpaChatRepositoryAdapter;
    private JpaLinkRepositoryAdapter jpaLinkRepositoryAdapter;

    private static final Chat chat1 = new Chat(1L, new ArrayList<>());
    private static final Chat chat2 = new Chat(2L, new ArrayList<>());

    private static final Link link1 = new Link(URI.create("https://github.com/user1/example"));
    private static final Link link2 = new Link(URI.create("https://github.com/user2/example"));
    private static final Link link3 = new Link(URI.create("https://stackoverflow.com/questions/123/what-is-question"));

    @BeforeEach
    void setUp() {
        jpaChatRepositoryAdapter = new JpaChatRepositoryAdapter(jpaChatRepository);
        jpaLinkRepositoryAdapter = new JpaLinkRepositoryAdapter(jpaLinkRepository);
        jpaChatRepositoryAdapter.save(chat1);
        jpaChatRepositoryAdapter.save(chat2);
    }

    @Test
    public void testFindAll() {
        jpaLinkRepositoryAdapter.save(chat1.getId(), link1);
        jpaLinkRepositoryAdapter.save(chat2.getId(), link2);
        jpaLinkRepositoryAdapter.save(chat2.getId(), link3);

        List<Link> links = jpaLinkRepositoryAdapter.findAll();

        assertThat(links.size()).isEqualTo(3);
        assertThat(links.stream().map(Link::getUrl).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(link1.getUrl(), link2.getUrl(), link3.getUrl()));
    }

    @Test
    public void testFindAllByChatId() {
        jpaLinkRepositoryAdapter.save(chat1.getId(), link1);
        jpaLinkRepositoryAdapter.save(chat2.getId(), link2);
        jpaLinkRepositoryAdapter.save(chat2.getId(), link3);

        List<Link> links = jpaLinkRepositoryAdapter.findAllByChatId(chat2.getId());

        assertThat(links.size()).isEqualTo(2);
        assertThat(links.stream().map(Link::getUrl).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(link2.getUrl(), link3.getUrl()));
    }

    @Test
    public void testFindAllOutdatedLinks() {
        link1.setLastCheckTime(OffsetDateTime.now().minusDays(1));
        link2.setLastCheckTime(OffsetDateTime.now());

        jpaLinkRepositoryAdapter.save(chat2.getId(), link1);
        jpaLinkRepositoryAdapter.save(chat2.getId(), link2);

        List<Link> foundLinks = jpaLinkRepositoryAdapter.findAllOutdatedLinks(1000);

        assertThat(foundLinks.stream().map(Link::getUrl).toList()).containsOnly(link1.getUrl());
    }

    @Test
    public void testFindByUrl() {
        jpaLinkRepositoryAdapter.save(chat1.getId(), link1);

        Optional<Link> foundLink = jpaLinkRepositoryAdapter.findByUrl(link1.getUrl().toString());

        assertThat(foundLink.isPresent()).isTrue();
        assertThat(foundLink.get().getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    public void testFindByChatIdAndUrl() {
        jpaLinkRepositoryAdapter.save(chat1.getId(), link1);
        Optional<Link> foundLink = jpaLinkRepositoryAdapter.findByChatIdAndUrl(chat1.getId(), link1.getUrl().toString());

        assertThat(foundLink).isPresent();
        assertThat(foundLink.get().getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    public void testSave() {
        jpaLinkRepositoryAdapter.save(chat1.getId(), link1);
        Optional<Link> foundLink = jpaLinkRepositoryAdapter.findByUrl(link1.getUrl().toString());

        assertThat(foundLink).isNotEmpty();
        assertThat(foundLink.get().getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    public void testDeleteLink() {
        jpaLinkRepositoryAdapter.save(chat1.getId(), link2);

        Optional<Link> foundLink = jpaLinkRepositoryAdapter.findByUrl(link2.getUrl().toString());
        assertThat(foundLink).isNotEmpty();

        jpaLinkRepositoryAdapter.delete(chat1.getId(), foundLink.get().getId());
        foundLink = jpaLinkRepositoryAdapter.findByUrl(link1.getUrl().toString());

        assertThat(foundLink).isEmpty();
    }

    @Test
    public void testSetLastCheckTime() {
        Link link = jpaLinkRepositoryAdapter.save(chat1.getId(), link1);
        OffsetDateTime newLastCheckTime = OffsetDateTime.now().minusDays(1);

        jpaLinkRepositoryAdapter.setLastCheckTime(link, newLastCheckTime);
        Optional<Link> updatedLinkOptional = jpaLinkRepositoryAdapter.findByUrl(link.getUrl().toString());

        assertThat(updatedLinkOptional.isPresent()).isTrue();
        assertThat(updatedLinkOptional.get().getLastCheckTime().toZonedDateTime()).isEqualTo(newLastCheckTime.toZonedDateTime());
    }
}
