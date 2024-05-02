package edu.java.scrapper.configuration.updatesender;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.service.kafka.ScrapperQueueProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
@RequiredArgsConstructor
public class ScrapperQueueProducerConfig {
    private final ApplicationConfig applicationConfig;

    @Bean
    public ScrapperQueueProducer scrapperQueueProducer(KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate) {
        return new ScrapperQueueProducer(applicationConfig.kafka().topicName(), kafkaTemplate);
    }
}
