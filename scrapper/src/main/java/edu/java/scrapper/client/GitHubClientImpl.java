package edu.java.scrapper.client;

import edu.java.scrapper.dto.RepositoryResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GitHubClientImpl implements GitHubClient {
    private String gitHubBaseUrl = "https://api.github.com";
    private final WebClient webClient;

    public GitHubClientImpl(String gitHubBaseUrl) {
        if (gitHubBaseUrl != null && !gitHubBaseUrl.isBlank()) {
           this.gitHubBaseUrl = gitHubBaseUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.gitHubBaseUrl).build();
    }

    @Override
    public RepositoryResponse fetchRepository(String username, String repository) {
        return webClient.get()
            .uri("/repos/{usr}/{repo}", username, repository)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(RuntimeException.class)
                .flatMap(e -> Mono.error(new RuntimeException("GitHub API exception"))))
            .bodyToMono(RepositoryResponse.class)
            .block();
    }
}
