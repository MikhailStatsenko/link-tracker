package edu.java.scrapper.client;

import edu.java.scrapper.dto.RepositoryResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClient {
    @Value("${api.default-url.github}")
    private String gitHubDefaultUrl;
    private String gitHubBaseUrl;
    private WebClient webClient;

    public GitHubClient(String gitHubBaseUrl) {
        this.gitHubBaseUrl = gitHubBaseUrl;
    }

    @PostConstruct
    public void init() {
        if (gitHubBaseUrl == null || gitHubBaseUrl.isBlank()) {
            gitHubBaseUrl = gitHubDefaultUrl;
        }
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
