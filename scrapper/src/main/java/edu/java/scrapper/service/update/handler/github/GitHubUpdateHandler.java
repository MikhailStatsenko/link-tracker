package edu.java.scrapper.service.update.handler.github;

import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.dto.external.github.EventResponse;
import edu.java.scrapper.exception.NoSuchRepositoryException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.service.update.handler.UpdateHandler;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubUpdateHandler implements UpdateHandler {
    private static final String HOST = "github.com";

    private final int usernameIndex = 2;
    private final int repositoryIndex = 3;

    private final List<Event> supportedEvents;
    private final GitHubClient gitHubClient;

    @Override
    public String host() {
        return HOST;
    }

    @Override
    public List<Optional<Update>> fetchUpdates(Link link) {
        String url = link.getUrl().toString();
        String[] urlParts = url.split("/+");

        List<Optional<Update>> updates = new ArrayList<>();
        try {
            List<EventResponse> events = fetchNewEvents(
                urlParts[usernameIndex],
                urlParts[repositoryIndex],
                link.getLastCheckTime()
            );

            events.forEach(event -> {
                for (Event e : supportedEvents) {
                    if (e.supports(event)) {
                        updates.add(e.process(event, link));
                    }
                }
            });
        } catch (NoSuchRepositoryException e) {
            log.error("При обработке обновлений возникла ошибка.\n{}", updates, e);
        }
        return updates;
    }

    private List<EventResponse> fetchNewEvents(String username, String repository, OffsetDateTime lastUpdateTime) {
        List<EventResponse> events = gitHubClient.fetchEvents(username, repository);
        return events.stream().filter(e -> e.getCreatedAt().isAfter(lastUpdateTime)).toList();
    }
}
