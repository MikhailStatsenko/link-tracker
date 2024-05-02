package edu.java.bot.configuration.retry;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public interface RetryProvider {
    ClientRetryConfig.RetryProperties retry();

    default Retry getRetry() {
        ClientRetryConfig.RetryProperties properties = retry();
        return switch (properties.retryPolicy()) {
            case CONSTANT -> Retry.of("constant", constantRetryConfig(properties));
            case LINEAR -> Retry.of("linear", linearRetryConfig(properties));
            case EXPONENTIAL -> Retry.of("exponential", exponentialRetryConfig(properties));
        };
    }

    private static RetryConfig constantRetryConfig(ClientRetryConfig.RetryProperties properties) {
        return RetryConfig.<RuntimeException>custom()
            .maxAttempts(properties.maxAttempts())
            .waitDuration(Duration.ofSeconds(properties.waitDuration()))
            .retryOnException(response -> properties.codes().contains(
                ((WebClientResponseException) response).getStatusCode()))
            .build();
    }

    private static RetryConfig linearRetryConfig(ClientRetryConfig.RetryProperties properties) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(properties.maxAttempts())
            .intervalFunction(IntervalFunction.of(
                Duration.ofSeconds(properties.waitDuration()),
                attempt -> properties.waitDuration() + properties.linearCoefficient() * attempt
            ))
            .retryOnException(response -> properties.codes().contains(
                ((WebClientResponseException) response).getStatusCode()))
            .build();
    }

    private static RetryConfig exponentialRetryConfig(ClientRetryConfig.RetryProperties properties) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(properties.maxAttempts())
            .intervalFunction(IntervalFunction.ofExponentialBackoff(
                properties.waitDuration(),
                properties.exponentialMultiplier()
            ))
            .retryOnException(response -> properties.codes().contains(
                ((WebClientResponseException) response).getStatusCode()))
            .build();
    }
}
