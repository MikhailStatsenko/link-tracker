package edu.java.bot.service.kafka;

import edu.java.bot.dto.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DlqProducer {
    @Value("${app.kafka.dql-topic}")
    private String dlqTopic;
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    public void send(LinkUpdateRequest update) {
        kafkaTemplate.send(dlqTopic, update);
    }
}
