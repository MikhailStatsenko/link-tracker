package edu.java.bot.configuration;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.configuration.retry.ClientRetryConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {
    private final ClientRetryConfig retryConfig;

    @Value("${api.scrapper.base-url}")
    private String scrapperBaseUrl;

    @Bean
    public ScrapperClient scrapperWebClient() {
        return new ScrapperClient(scrapperBaseUrl, retryConfig.scrapper().getRetry());
    }
}
