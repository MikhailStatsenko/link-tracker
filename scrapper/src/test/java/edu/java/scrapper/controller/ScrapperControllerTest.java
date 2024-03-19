package edu.java.scrapper.controller;

import edu.java.scrapper.dto.api.request.AddLinkRequest;
import edu.java.scrapper.dto.api.request.RemoveLinkRequest;
import edu.java.scrapper.dto.api.response.LinkResponse;
import edu.java.scrapper.dto.api.response.ListLinksResponse;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScrapperControllerTest {
    @Mock
    private Link mockLink;

    @Mock
    private ChatService chatService;

    @Mock
    private LinkService linkService;

    @InjectMocks
    private ScrapperController scrapperController;

    private final Long testChatId = 1L;
    private final URI testUrl = URI.create("http://github.com/user/repo");

    @BeforeEach
    void setUp() {
        when(mockLink.getId()).thenReturn(1L);
        when(mockLink.getUrl()).thenReturn(testUrl);
    }

    @Test
    void testDeleteLink() {
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(testUrl);
        doNothing().when(linkService).remove(testChatId, removeLinkRequest.link());

        ResponseEntity<LinkResponse> responseEntity = scrapperController.deleteLink(testChatId, removeLinkRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(linkService, times(1)).remove(testChatId, removeLinkRequest.link());
    }

    @Test
    void testGetLinks() {
        List<Link> mockLinks = List.of(mockLink);
        when(linkService.listAll(testChatId)).thenReturn(mockLinks);

        ResponseEntity<ListLinksResponse> responseEntity = scrapperController.getLinks(testChatId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().size()).isEqualTo(1);
        assertThat(responseEntity.getBody().links()).hasSize(1);
    }

    @Test
    void testAddLink() {
        AddLinkRequest addLinkRequest = new AddLinkRequest(testUrl);
        when(linkService.add(testChatId, addLinkRequest.link())).thenReturn(mockLink);

        ResponseEntity<LinkResponse> responseEntity = scrapperController.addLink(testChatId, addLinkRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void testDeleteChat() {
        doNothing().when(chatService).unregister(testChatId);

        ResponseEntity<Void> responseEntity = scrapperController.deleteChat(testChatId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(chatService, times(1)).unregister(testChatId);
    }

    @Test
    void testAddChat() {
        doNothing().when(chatService).register(testChatId);

        ResponseEntity<Void> responseEntity = scrapperController.addChat(testChatId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(chatService, times(1)).register(testChatId);
    }
}


