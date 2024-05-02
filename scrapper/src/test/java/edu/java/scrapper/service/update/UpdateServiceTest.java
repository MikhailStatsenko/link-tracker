package edu.java.scrapper.service.update;

import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.update.handler.UpdateHandler;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateServiceTest {
    private final long interval = 1000L;
    private final long chatId1 = 1L;
    private final long chatId2 = 2L;
    private final String link1 = "example.com/link1";
    private final String link2 = "example.com/link2";
    private final Update update1 = new Update(1, link1, "Update 1 description");
    private final Update update2 = new Update(2, link2, "Update 2 description");

    @Mock
    private ChatService chatService;

    @Mock
    private LinkService linkService;

    @Mock
    private UpdateHandler updateHandler1;

    @Mock
    private UpdateHandler updateHandler2;

    @InjectMocks
    private UpdateService updateService;

    @BeforeEach
    void setUp() {
        updateService = new UpdateService(chatService, linkService, List.of(updateHandler1, updateHandler2));
    }

    @Test
    void testFetchAllUpdates() {
        List<Link> links = List.of(
            new Link(URI.create(link1)),
            new Link(URI.create(link2))
        );

        when(linkService.findAllOutdatedLinks(interval)).thenReturn(links);
        when(updateHandler1.supports(URI.create(link1))).thenReturn(true);
        when(updateHandler2.supports(URI.create(link2))).thenReturn(true);
        when(updateHandler1.fetchUpdates(any())).thenReturn(List.of(Optional.of(update1)));
        when(updateHandler2.fetchUpdates(any())).thenReturn(List.of(Optional.of(update2)));
        when(chatService.findAllChatIdsByLinkId(update1.linkId())).thenReturn(List.of(chatId1));
        when(chatService.findAllChatIdsByLinkId(update2.linkId())).thenReturn(List.of(chatId2));

        List<LinkUpdateRequest> result = updateService.fetchAllUpdates(interval);

        System.out.println(result);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).tgChatIds().size());
        assertEquals(1, result.get(1).tgChatIds().size());
        verify(linkService, times(2)).setLastCheckTime(any(), any());
    }

}
