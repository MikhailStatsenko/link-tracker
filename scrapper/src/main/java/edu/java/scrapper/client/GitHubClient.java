package edu.java.scrapper.client;

import edu.java.scrapper.dto.external.github.ErrorResponse;
import edu.java.scrapper.dto.external.github.EventResponse;
import edu.java.scrapper.exception.NoSuchRepositoryException;
import io.github.resilience4j.retry.Retry;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GitHubClient {
    private final Retry retry;
    private final int eventsCount;
    private final String accessToken;
    private final WebClient webClient;

    public GitHubClient(String gitHubBaseUrl, String accessToken,  int eventsCount, Retry retry) {
        this.retry = retry;
        this.webClient = WebClient.create(gitHubBaseUrl);
        this.accessToken = accessToken;
        this.eventsCount = eventsCount;
    }

    public List<EventResponse> fetchEvents(String username, String repository) {
        return Retry.decorateSupplier(retry, () -> {
            Mono<List<EventResponse>> events = webClient.get()
                .uri("/repos/{usr}/{repo}/events?per_page={count}", username, repository, eventsCount)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(
                    HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(ErrorResponse.class).map(NoSuchRepositoryException::new)
                ).bodyToFlux(EventResponse.class).collectList();
            return events.block();
        }).get();
    }
}
