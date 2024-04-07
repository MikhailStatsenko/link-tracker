package edu.java.scrapper.configuration.updatesender;

import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.configuration.retry.ClientRetryConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
@RequiredArgsConstructor
public class BotClientConfig {
    private final ClientRetryConfig retryConfig;

    @Value("${api.bot.base-url}")
    private String botBaseUrl;

    @Bean
    public BotClient botWebClient() {
        return new BotClient(botBaseUrl,  retryConfig.bot().getRetry());
    }
}
