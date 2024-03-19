package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.exception.LinkAlreadyTrackedException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.JdbcLinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcLinkServiceTest {
    @Mock
    private Link mockLink;

    @Mock
    private JdbcLinkRepository linkRepository;

    @InjectMocks
    private JdbcLinkService linkService;

    private final long testChatId = 1L;
    private final URI testUrl = URI.create("http://github.com/user/repo");

    @Test
    void testListAll() {
        List<Link> expectedLinks = new ArrayList<>();
        expectedLinks.add(mockLink);
        when(linkRepository.findAllByChatId(testChatId)).thenReturn(expectedLinks);

        List<Link> actualLinks = linkService.listAll(testChatId);

        assertThat(actualLinks).isEqualTo(expectedLinks);
        verify(linkRepository).findAllByChatId(testChatId);
    }

    @Test
    void testListAllOutdatedLinks() {
        long interval = 100L;
        List<Link> expectedLinks = new ArrayList<>();
        expectedLinks.add(mockLink);
        when(linkRepository.findAllOutdatedLinks(interval)).thenReturn(expectedLinks);

        List<Link> actualLinks = linkService.listAllOutdatedLinks(interval);

        assertThat(actualLinks).isEqualTo(expectedLinks);
        verify(linkRepository).findAllOutdatedLinks(interval);
    }

    @Test
    void testAdd() {
        when(linkRepository.findByChatIdAndUrl(testChatId, testUrl.toString())).thenReturn(Optional.empty());
        when(linkRepository.save(eq(testChatId), any(Link.class))).thenAnswer(invocation -> {
            Link linkArgument = invocation.getArgument(1);
            linkArgument.setId(1L);
            return linkArgument;
        });

        Link addedLink = linkService.add(testChatId, testUrl);

        assertThat(addedLink).isNotNull();
        assertThat(addedLink.getUrl()).isEqualTo(testUrl);
        verify(linkRepository).save(eq(testChatId), any(Link.class));
    }

    @Test
    void testAddWhenLinkAlreadyTracked() {
        when(linkRepository.findByChatIdAndUrl(testChatId, testUrl.toString()))
            .thenReturn(Optional.of(new Link(testUrl)));

        assertThrows(LinkAlreadyTrackedException.class, () -> linkService.add(testChatId, testUrl));
    }

    @Test
    void testSetLastCheckTime() {
        OffsetDateTime lastCheckTime = OffsetDateTime.now();

        linkService.setLastCheckTime(mockLink, lastCheckTime);

        verify(linkRepository).setLastCheckTime(mockLink, lastCheckTime);
    }

    @Test
    void testRemove() {
        when(linkRepository.findByUrl(testUrl.toString())).thenReturn(Optional.of(mockLink));

        linkService.remove(testChatId, testUrl);

        verify(linkRepository).delete(testChatId, mockLink.getId());
    }

    @Test
    void testRemoveLinkNotFound() {
        when(linkRepository.findByUrl(testUrl.toString())).thenReturn(Optional.empty());

        assertThrows(LinkAlreadyTrackedException.class, () -> linkService.remove(testChatId, testUrl));
    }
}

