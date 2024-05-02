package edu.java.scrapper.service.update.handler.github;

import com.fasterxml.jackson.databind.JsonNode;
import edu.java.scrapper.dto.external.github.EventResponse;
import java.time.OffsetDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PullRequestEventTest {
    @Mock
    JsonNode mockJsonNode;

    @InjectMocks
    PullRequestEvent pullRequestEvent;

    @Test
    public void testType() {
        assertThat(pullRequestEvent.type()).isEqualTo("PullRequestEvent");
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void testGetDescription(String actionType, String expectedDescription) {
        setPullRequestPayload(actionType);

        EventResponse eventResponse = new EventResponse(
            "PullRequestEvent",
            OffsetDateTime.now(),
            new EventResponse.Actor("username"),
            new EventResponse.Repo("repository"),
            mockJsonNode);

        assertThat(pullRequestEvent.getDescription(eventResponse))
            .isEqualTo(expectedDescription);
    }

    private static Stream<Arguments> provideParameters() {
        String text = "Пользователь username %s в репозитории repository от ветки headBranch к ветке baseBranch";
        return Stream.of(
            Arguments.of("opened", String.format(text, "открыл пулл реквест")),
            Arguments.of("edited", String.format(text, "изменил пулл реквест")),
            Arguments.of("closed", String.format(text, "закрыл пулл реквест")),
            Arguments.of("reopened", String.format(text, "переоткрыл пулл реквест")),
            Arguments.of("ready_for_review", String.format(text, "пометил пулл реквест как готовый для рассмотрения")),
            Arguments.of("wrong action", String.format(text, "произвел действие в пулл реквесте"))
        );
    }

    private void setPullRequestPayload(String actionType) {
        JsonNode action = mock(JsonNode.class);
        when(mockJsonNode.path("action")).thenReturn(action);
        when(action.asText()).thenReturn(actionType);

        JsonNode pullRequest = mock(JsonNode.class);
        when(mockJsonNode.path("pull_request")).thenReturn(pullRequest);

        JsonNode base = mock(JsonNode.class);
        when(pullRequest.path("base")).thenReturn(base);
        JsonNode baseRef = mock(JsonNode.class);
        when(base.path("ref")).thenReturn(baseRef);
        when(baseRef.asText()).thenReturn("baseBranch");

        JsonNode head = mock(JsonNode.class);
        when(pullRequest.path("head")).thenReturn(head);
        JsonNode headRef = mock(JsonNode.class);
        when(head.path("ref")).thenReturn(headRef);
        when(headRef.asText()).thenReturn("headBranch");
    }
}

