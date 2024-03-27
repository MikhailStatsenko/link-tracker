package edu.java.scrapper.configuration;

import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.StackOverflowClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Value("${api.github.base-url:${api.github.default-url}}")
    private String gitHubBaseUrl;

    @Value("${api.stackoverflow.base-url:${api.stackoverflow.default-url}}")
    private String stackOverflowBaseUrl;

    @Value("${api.bot.base-url}")
    private String botBaseUrl;

    @Value("${api.github.events-count}")
    int githubEventsCount;

    @Value("${api.github.token}")
    String gitHubToken;

    @Bean
    public GitHubClient gitHubWebClient() {
        return new GitHubClient(gitHubBaseUrl, gitHubToken, githubEventsCount);
    }

    @Bean
    public StackOverflowClient stackOverflowWebClient() {
        return new StackOverflowClient(stackOverflowBaseUrl);
    }

    @Bean
    public BotClient botWebClient() {
        return new BotClient(botBaseUrl);
    }
}
