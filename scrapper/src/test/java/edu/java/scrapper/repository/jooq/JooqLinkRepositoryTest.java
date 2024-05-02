package edu.java.scrapper.repository.jooq;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.time.OffsetDateTime;
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

@SpringBootTest
@Transactional
class JooqLinkRepositoryTest extends IntegrationTest {
    @Autowired
    DSLContext dsl;
    private JooqChatRepository jooqChatRepository;
    private JooqLinkRepository jooqLinkRepository;

    private static final Chat chat1 = new Chat(1L, new ArrayList<>());
    private static final Chat chat2 = new Chat(2L, new ArrayList<>());

    private static final Link link1 = new Link(URI.create("https://github.com/user1/example"));
    private static final Link link2 = new Link(URI.create("https://github.com/user2/example"));
    private static final Link link3 = new Link(URI.create("https://stackoverflow.com/questions/123/what-is-question"));

    @BeforeEach
    public void setUp() {
        jooqChatRepository = new JooqChatRepository(dsl);
        jooqLinkRepository = new JooqLinkRepository(dsl);
        jooqChatRepository.save(chat1);
        jooqChatRepository.save(chat2);

    }

    @Test
    public void testFindAll() {
        jooqLinkRepository.save(chat1.getId(), link1);
        jooqLinkRepository.save(chat2.getId(), link2);
        jooqLinkRepository.save(chat2.getId(), link3);

        List<Link> links = jooqLinkRepository.findAll();

        assertThat(links.size()).isEqualTo(3);
        assertThat(links.stream().map(Link::getUrl).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(link1.getUrl(), link2.getUrl(), link3.getUrl()));
    }

    @Test
    public void testFindAllByChatId() {
        jooqLinkRepository.save(chat1.getId(), link1);
        jooqLinkRepository.save(chat2.getId(), link2);
        jooqLinkRepository.save(chat2.getId(), link3);

        List<Link> links = jooqLinkRepository.findAllByChatId(chat2.getId());

        assertThat(links.size()).isEqualTo(2);
        assertThat(links.stream().map(Link::getUrl).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(link2.getUrl(), link3.getUrl()));
    }

    @Test
    public void testFindAllOutdatedLinks() {
        link1.setLastCheckTime(OffsetDateTime.now().minusDays(1));
        link2.setLastCheckTime(OffsetDateTime.now());

        jooqLinkRepository.save(chat2.getId(), link1);
        jooqLinkRepository.save(chat2.getId(), link2);

        List<Link> foundLinks = jooqLinkRepository.findAllOutdatedLinks(1000);

        assertThat(foundLinks.stream().map(Link::getUrl).toList()).containsOnly(link1.getUrl());
    }

    @Test
    public void testFindByUrl() {
        jooqLinkRepository.save(chat1.getId(), link1);

        Optional<Link> foundLink = jooqLinkRepository.findByUrl(link1.getUrl().toString());

        assertThat(foundLink.isPresent()).isTrue();
        assertThat(foundLink.get().getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    public void testFindByChatIdAndUrl() {
        jooqLinkRepository.save(chat1.getId(), link1);
        Optional<Link> foundLink = jooqLinkRepository.findByChatIdAndUrl(chat1.getId(), link1.getUrl().toString());

        assertThat(foundLink).isPresent();
        assertThat(foundLink.get().getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    public void testSave() {
        jooqLinkRepository.save(chat1.getId(), link1);
        Optional<Link> foundLink = jooqLinkRepository.findByUrl(link1.getUrl().toString());

        assertThat(foundLink).isNotEmpty();
        assertThat(foundLink.get().getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    public void testDeleteLink() {
        jooqLinkRepository.save(chat1.getId(), link2);

        Optional<Link> foundLink = jooqLinkRepository.findByUrl(link2.getUrl().toString());
        assertThat(foundLink).isNotEmpty();

        jooqLinkRepository.delete(chat1.getId(), foundLink.get().getId());
        foundLink = jooqLinkRepository.findByUrl(link1.getUrl().toString());

        assertThat(foundLink).isEmpty();
    }

    @Test
    public void testSetLastCheckTime() {
        Link link = jooqLinkRepository.save(chat1.getId(), link1);
        OffsetDateTime newLastCheckTime = OffsetDateTime.now().minusDays(1);

        jooqLinkRepository.setLastCheckTime(link, newLastCheckTime);
        Optional<Link> updatedLinkOptional = jooqLinkRepository.findByUrl(link.getUrl().toString());

        assertThat(updatedLinkOptional.isPresent()).isTrue();
        assertThat(updatedLinkOptional.get().getLastCheckTime().toZonedDateTime()).isEqualToIgnoringNanos(newLastCheckTime.toZonedDateTime());
    }
}
