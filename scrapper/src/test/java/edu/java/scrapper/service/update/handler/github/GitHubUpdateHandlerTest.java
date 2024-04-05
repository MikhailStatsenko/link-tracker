package edu.java.scrapper.service.update.handler.github;

import com.fasterxml.jackson.databind.JsonNode;
import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.dto.external.github.EventResponse;
import edu.java.scrapper.exception.NoSuchRepositoryException;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubUpdateHandlerTest {
    private final Link link = new Link(URI.create("https://github.com/user/repository"));
    private final Update update = new Update(link.getId(), link.getUrl().toString(), "description");
    private final EventResponse eventResponse = new EventResponse(
        "pushEvent",
        OffsetDateTime.now(),
        new EventResponse.Actor("MikhailStatsenko"),
        new EventResponse.Repo("MikhailStatsenko/test"),
        mock(JsonNode.class));

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    Event event;

    @InjectMocks
    GitHubUpdateHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GitHubUpdateHandler(List.of(event), gitHubClient);
    }

    @Test
    public void testGetHost() {
        assertThat(handler.host()).isEqualTo("github.com");
    }

    @Test
    void testFetchUpdatesReturnsUpdatesWhenEventsAfterLastCheckTime() {
        link.setLastCheckTime(OffsetDateTime.now().minusDays(1));
        List<EventResponse> events = Collections.singletonList(eventResponse);

        when(event.supports(eventResponse)).thenReturn(true);
        when(event.process(eventResponse, link)).thenReturn(Optional.of(update));
        when(gitHubClient.fetchEvents("user", "repository")).thenReturn(events);

        List<Optional<Update>> updates = handler.fetchUpdates(link);
        assertThat(updates).hasSize(1);
        assertThat(updates.getFirst()).isPresent();
    }

    @Test
    void testFetchUpdatesReturnsUpdatesWhenEventsAfterLastCheckTimeEventNotSupported() {
        link.setLastCheckTime(OffsetDateTime.now().minusDays(1));
        List<EventResponse> events = Collections.singletonList(eventResponse);

        when(gitHubClient.fetchEvents("user", "repository")).thenReturn(events);

        List<Optional<Update>> updates = handler.fetchUpdates(link);
        assertThat(updates).hasSize(0);
    }

    @Test
    void testFetchUpdatesReturnsUpdatesWhenEventsBeforeLastCheckTime() {
        link.setLastCheckTime(OffsetDateTime.now().plusDays(1));
        List<EventResponse> events = Collections.singletonList(eventResponse);

        when(gitHubClient.fetchEvents("user", "repository")).thenReturn(events);

        List<Optional<Update>> updates = handler.fetchUpdates(link);
        assertThat(updates).isEmpty();
    }

    @Test
    void testFetchUpdatesHandlesNoSuchRepositoryException() {
        link.setLastCheckTime(OffsetDateTime.now().minusDays(1));
        when(gitHubClient.fetchEvents(anyString(), anyString())).thenThrow(NoSuchRepositoryException.class);

        List<Optional<Update>> updates = handler.fetchUpdates(link);

        assertThat(updates).isEmpty();
    }
}
