package edu.java.scrapper.configuration;

import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.StackOverflowClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Value("${api.base-url.github:${api.default-url.github}}")
    private String gitHubBaseUrl;
    @Value("${api.base-url.stackoverflow:${api.default-url.stackoverflow}}")
    private String stackOverflowBaseUrl;

    @Bean
    public GitHubClient gitHubWebClient() {
        return new GitHubClient(gitHubBaseUrl);
    }

    @Bean
    public StackOverflowClient stackOverflowWebClient() {
        return new StackOverflowClient(stackOverflowBaseUrl);
    }
}
