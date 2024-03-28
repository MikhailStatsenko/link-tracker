package edu.java.scrapper.service.update.handler.github;

import com.fasterxml.jackson.databind.JsonNode;
import edu.java.scrapper.dto.external.github.EventResponse;
import org.springframework.stereotype.Component;

@Component
public class PullRequestEvent implements Event {
    private static final String EVENT_TYPE = "PullRequestEvent";
    private static final String MESSAGE = "Пользователь %s %s в репозитории %s от ветки %s к ветке %s";

    private static final String REF = "ref";

    @Override
    public String type() {
        return EVENT_TYPE;
    }

    @Override
    public String getDescription(EventResponse eventResponse) {
        JsonNode pullRequest = eventResponse.getPayload().path("pull_request");
        String actionType = eventResponse.getPayload().path("action").asText();
        String baseRef = pullRequest.path("base").path(REF).asText();
        String headRef = pullRequest.path("head").path(REF).asText();

        return String.format(
            MESSAGE,
            eventResponse.getActor().login(),
            getActionByActionType(actionType),
            eventResponse.getRepo().name(),
            headRef,
            baseRef
        );
    }

    private String getActionByActionType(String actionType) {
        return switch (actionType) {
            case "opened" -> "открыл пулл реквест";
            case "edited" -> "изменил пулл реквест";
            case "closed" -> "закрыл пулл реквест";
            case "reopened" -> "переоткрыл пулл реквест";
            case "ready_for_review" -> "пометил пулл реквест как готовый для рассмотрения";
            default -> "произвел действие в пулл реквесте";
        };
    }
}
