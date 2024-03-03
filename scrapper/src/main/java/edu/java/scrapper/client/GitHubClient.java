package edu.java.scrapper.client;

import edu.java.scrapper.dto.external.RepositoryResponse;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClient {
    private final WebClient webClient;

    public GitHubClient(String gitHubBaseUrl) {
        this.webClient = WebClient.create(gitHubBaseUrl);
    }

    public RepositoryResponse fetchRepository(String username, String repository) {
        return webClient.get()
            .uri("/repos/{usr}/{repo}", username, repository)
            .retrieve()
            .bodyToMono(RepositoryResponse.class)
            .block();
    }
}
