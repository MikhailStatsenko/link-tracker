package edu.java.scrapper.service.update.handler.github;

import com.fasterxml.jackson.databind.JsonNode;
import edu.java.scrapper.dto.external.github.EventResponse;
import org.springframework.stereotype.Component;

@Component
public class PullRequestEvent implements Event {
    private static final String EVENT_TYPE = "PullRequestEvent";
    private static final String MESSAGE = "Пользователь %s создал пулл реквест в репозитории %s от ветки %s к ветке %s";

    private static final String REF = "ref";

    @Override
    public String type() {
        return EVENT_TYPE;
    }

    @Override
    public String getDescription(EventResponse eventResponse) {
        JsonNode pullRequest = eventResponse.getPayload().path("pull_request");
        String baseRef = pullRequest.path("base").path(REF).asText();
        String headRef = pullRequest.path("head").path(REF).asText();

        return String.format(
            MESSAGE,
            eventResponse.getActor().login(),
            eventResponse.getRepo().name(),
            headRef,
            baseRef
        );
    }
}
