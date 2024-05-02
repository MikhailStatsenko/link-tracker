package edu.java.bot.configuration.retry;

import java.util.Set;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;

@ConfigurationProperties(prefix = "api")
public record ClientRetryConfig(
    ScrapperClientRetryConfig scrapper) {

    public record ScrapperClientRetryConfig(RetryProperties retry) implements RetryProvider {
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
