package edu.java.scrapper.service.update.handler.stackoverflow;

import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.dto.external.QuestionResponse;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StackoverflowUpdateHandlerTest {
    private final Link link = new Link(URI.create("https://stackoverflow.com/questions/12345"));
    private final QuestionResponse response = new QuestionResponse(Collections.singletonList(new QuestionResponse.QuestionResponseItem(12345, OffsetDateTime.now())));

    @Mock
    private StackOverflowClient stackOverflowClient;

    @InjectMocks
    StackoverflowUpdateHandler handler;

    @Test
    public void testGetHost() {
        assertThat(handler.host()).isEqualTo("stackoverflow.com");
    }

    @Test
    void testFetchUpdatesReturnsUpdateWhenActivityDateAfterLastCheckTime() {
        link.setLastCheckTime(OffsetDateTime.now().minusDays(1));

        when(stackOverflowClient.fetchQuestion(12345L)).thenReturn(response);

        List<Optional<Update>> updates = handler.fetchUpdates(link);

        assertThat(updates).hasSize(1);
        assertThat(updates.getFirst()).isPresent();
    }

    @Test
    void testFetchUpdatesReturnsUpdateWhenActivityDateBeforeLastCheckTime() {
        link.setLastCheckTime(OffsetDateTime.now().plusDays(1));

        when(stackOverflowClient.fetchQuestion(12345L)).thenReturn(response);

        List<Optional<Update>> updates = handler.fetchUpdates(link);

        assertThat(updates.getFirst()).isEmpty();
    }
}
