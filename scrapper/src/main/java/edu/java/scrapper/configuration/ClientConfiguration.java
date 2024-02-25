package edu.java.scrapper.configuration;

import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.GitHubClientImpl;
import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.client.StackOverflowClientImpl;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Setter
@ConfigurationProperties(prefix = "api.base-url", ignoreUnknownFields = false)
public class ClientConfiguration {
    @NotNull
    private String gitHub;
    @NotNull
    private String stackOverflow;

    @Bean
    public GitHubClient gitHubWebClient() {
        return new GitHubClientImpl(gitHub);
    }

    @Bean
    public StackOverflowClient stackOverflowWebClient() {
        return new StackOverflowClientImpl(stackOverflow);
    }

    public String getGitHubBaseUrl() {
        return gitHub;
    }

    public String getStackOverflowBaseUrl() {
        return stackOverflow;
    }
}
