package edu.java.scrapper.service.update.handler.github;

import edu.java.scrapper.dto.external.github.EventResponse;
import org.springframework.stereotype.Component;

@Component
public class PushEvent implements Event {
    private static final String EVENT_TYPE = "PushEvent";
    private static final String MESSAGE = "Пользователь %s отправил изменения в репозиторий %s";

    @Override
    public String type() {
        return EVENT_TYPE;
    }

    @Override
    public String getDescription(EventResponse eventResponse) {
        return String.format(
            MESSAGE,
            eventResponse.getActor().login(),
            eventResponse.getRepo().name()
        );
    }
}
