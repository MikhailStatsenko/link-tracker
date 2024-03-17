package edu.java.scrapper.service.update.handler;

import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.util.Optional;

public interface UpdateHandler {
    String host();

    Optional<Update> fetchUpdate(Link link);

    default boolean supports(URI uri) {
        return uri.getHost() != null && uri.getHost().equals(host());
    }
}
