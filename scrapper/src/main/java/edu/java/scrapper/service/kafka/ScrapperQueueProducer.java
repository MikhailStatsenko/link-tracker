package edu.java.scrapper.service.kafka;

import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.service.update.UpdateSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
public class ScrapperQueueProducer implements UpdateSender {
    private final String topicName;
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    public ScrapperQueueProducer(String topicName, KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUpdates(LinkUpdateRequest update) {
        log.debug("Sending kafka update {}", update);
        kafkaTemplate.send(topicName, update);
    }
}
