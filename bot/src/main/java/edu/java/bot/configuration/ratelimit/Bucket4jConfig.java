package edu.java.bot.configuration.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix = "rate-limit")
public record Bucket4jConfig(
    int tokenCount,
    int refillPeriod
) {
    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor(tokenCount, refillPeriod);
    }
}
