package edu.java.scrapper.configuration;

import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.configuration.retry.ClientRetryConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ClientConfiguration {
    private final ClientRetryConfig retryConfig;

    @Value("${api.github.base-url:${api.github.default-url}}")
    private String gitHubBaseUrl;

    @Value("${api.stackoverflow.base-url:${api.stackoverflow.default-url}}")
    private String stackOverflowBaseUrl;

    @Value("${api.github.events-count}")
    int githubEventsCount;

    @Value("${api.github.token}")
    String gitHubToken;

    @Bean
    public GitHubClient gitHubWebClient() {
        return new GitHubClient(gitHubBaseUrl, gitHubToken, githubEventsCount, retryConfig.github().getRetry());
    }

    @Bean
    public StackOverflowClient stackOverflowWebClient() {
        return new StackOverflowClient(stackOverflowBaseUrl, retryConfig.stackoverflow().getRetry());
    }
}
