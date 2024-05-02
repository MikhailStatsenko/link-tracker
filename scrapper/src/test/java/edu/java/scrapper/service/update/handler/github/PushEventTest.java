package edu.java.scrapper.service.update.handler.github;

import com.fasterxml.jackson.databind.JsonNode;
import edu.java.scrapper.dto.external.github.EventResponse;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class PushEventTest {
    @InjectMocks
    private PushEvent pushEvent;

    @Test
    public void testType() {
        assertThat(pushEvent.type()).isEqualTo("PushEvent");
    }

    @Test
    public void testGetDescription() {
        EventResponse eventResponse = new EventResponse(
            "PushEvent",
            OffsetDateTime.now(),
            new EventResponse.Actor("username"),
            new EventResponse.Repo("repository"),
            mock(JsonNode.class));

        assertThat(pushEvent.getDescription(eventResponse))
            .isEqualTo("Пользователь username отправил изменения в репозиторий repository");

    }
}

