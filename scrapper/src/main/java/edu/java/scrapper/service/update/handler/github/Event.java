package edu.java.scrapper.service.update.handler.github;

import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.dto.external.github.EventResponse;
import edu.java.scrapper.model.Link;
import java.util.Optional;

public interface Event {
    String type();

    String getDescription(EventResponse eventResponse);

    default boolean supports(EventResponse response) {
        return response.getType() != null && response.getType().equals(type());
    }

    default Optional<Update> process(EventResponse eventResponse, Link link) {
        return Optional.of(new Update(link.getId(), link.getUrl().toString(), getDescription(eventResponse)));
    }
}
