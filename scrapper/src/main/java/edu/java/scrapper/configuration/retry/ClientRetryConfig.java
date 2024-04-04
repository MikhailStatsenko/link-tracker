package edu.java.scrapper.configuration.retry;

import java.util.Set;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;

@ConfigurationProperties(prefix = "api")
public record ClientRetryConfig(
    BotClientRetryConfig bot,
    GitHubClientRetryConfig github,
    StackOverflowClientRetryConfig stackoverflow) {

    public record BotClientRetryConfig(RetryProperties retry) implements RetryProvider {
    }

    public record GitHubClientRetryConfig(RetryProperties retry) implements RetryProvider {
    }

    public record StackOverflowClientRetryConfig(RetryProperties retry) implements RetryProvider {
    }

    @Builder
    public record RetryProperties(
        RetryPolicy retryPolicy,
        int maxAttempts,
        int waitDuration,
        int linearCoefficient,
        double exponentialMultiplier,
        int exponentialArg,
        Set<HttpStatus> codes
    ) {
    }
}
