package edu.java.scrapper.service.update.handler;

import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface UpdateHandler {
    String host();

    List<Optional<Update>> fetchUpdates(Link link);

    default boolean supports(URI uri) {
        return uri.getHost() != null && uri.getHost().equals(host());
    }
}
