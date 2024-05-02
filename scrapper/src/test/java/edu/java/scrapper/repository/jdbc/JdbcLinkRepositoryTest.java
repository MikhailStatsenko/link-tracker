package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class JdbcLinkRepositoryTest extends IntegrationTest {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private JdbcChatRepository jdbcChatRepository;
    private JdbcLinkRepository jdbcLinkRepository;

    private static final Chat chat1 = new Chat(1L, new ArrayList<>());
    private static final Chat chat2 = new Chat(2L, new ArrayList<>());

    private static final Link link1 = new Link(URI.create("https://github.com/user1/example"));
    private static final Link link2 = new Link(URI.create("https://github.com/user2/example"));
    private static final Link link3 = new Link(URI.create("https://stackoverflow.com/questions/123/what-is-question"));

    @BeforeEach
    public void setUp() {
        jdbcChatRepository = new JdbcChatRepository(jdbcTemplate);
        jdbcLinkRepository = new JdbcLinkRepository(jdbcTemplate);
        jdbcChatRepository.save(chat1);
        jdbcChatRepository.save(chat2);
    }

    @Test
    public void testFindAll() {
        jdbcLinkRepository.save(chat1.getId(), link1);
        jdbcLinkRepository.save(chat2.getId(), link2);
        jdbcLinkRepository.save(chat2.getId(), link3);

        List<Link> links = jdbcLinkRepository.findAll();

        assertThat(links.size()).isEqualTo(3);
        assertThat(links.stream().map(Link::getUrl).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(link1.getUrl(), link2.getUrl(), link3.getUrl()));
    }

    @Test
    public void testFindAllByChatId() {
        jdbcLinkRepository.save(chat1.getId(), link1);
        jdbcLinkRepository.save(chat2.getId(), link2);
        jdbcLinkRepository.save(chat2.getId(), link3);

        List<Link> links = jdbcLinkRepository.findAllByChatId(chat2.getId());

        assertThat(links.size()).isEqualTo(2);
        assertThat(links.stream().map(Link::getUrl).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(link2.getUrl(), link3.getUrl()));
    }

    @Test
    public void testFindAllOutdatedLinks() {
        link1.setLastCheckTime(OffsetDateTime.now().minusDays(1));
        link2.setLastCheckTime(OffsetDateTime.now());

        jdbcLinkRepository.save(chat2.getId(), link1);
        jdbcLinkRepository.save(chat2.getId(), link2);

        List<Link> foundLinks = jdbcLinkRepository.findAllOutdatedLinks(1000);

        assertThat(foundLinks.stream().map(Link::getUrl).toList()).containsOnly(link1.getUrl());
    }

    @Test
    public void testFindByUrl() {
        jdbcLinkRepository.save(chat1.getId(), link1);

        Optional<Link> foundLink = jdbcLinkRepository.findByUrl(link1.getUrl().toString());

        assertThat(foundLink.isPresent()).isTrue();
        assertThat(foundLink.get().getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    public void testFindByChatIdAndUrl() {
        jdbcLinkRepository.save(chat1.getId(), link1);
        Optional<Link> foundLink = jdbcLinkRepository.findByChatIdAndUrl(chat1.getId(), link1.getUrl().toString());

        assertThat(foundLink).isPresent();
        assertThat(foundLink.get().getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    public void testSave() {
        jdbcLinkRepository.save(chat1.getId(), link1);
        Optional<Link> foundLink = jdbcLinkRepository.findByUrl(link1.getUrl().toString());

        assertThat(foundLink).isNotEmpty();
        assertThat(foundLink.get().getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    public void testDeleteLink() {
        jdbcLinkRepository.save(chat1.getId(), link2);

        Optional<Link> foundLink = jdbcLinkRepository.findByUrl(link2.getUrl().toString());
        assertThat(foundLink).isNotEmpty();

        jdbcLinkRepository.delete(chat1.getId(), foundLink.get().getId());
        foundLink = jdbcLinkRepository.findByUrl(link1.getUrl().toString());

        assertThat(foundLink).isEmpty();
    }

    @Test
    public void testSetLastCheckTime() {
        Link link = jdbcLinkRepository.save(chat1.getId(), link1);
        OffsetDateTime newLastCheckTime = OffsetDateTime.now().minusDays(1);

        jdbcLinkRepository.setLastCheckTime(link, newLastCheckTime);
        Optional<Link> updatedLinkOptional = jdbcLinkRepository.findByUrl(link.getUrl().toString());

        assertThat(updatedLinkOptional.isPresent()).isTrue();
        assertThat(updatedLinkOptional.get().getLastCheckTime().toZonedDateTime()).isEqualToIgnoringNanos(newLastCheckTime.toZonedDateTime());
    }
}
