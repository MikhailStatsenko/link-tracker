package edu.java.bot.service.kafka;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.UpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotQueueConsumer {
    private final DlqProducer dlqProducer;
    private final UpdateService updateService;

    @KafkaListener(
        topics = "${app.kafka.topic-name}",
        groupId = "${app.kafka.consumer.group-id}"
    )
    public void listen(LinkUpdateRequest update) {
        try {
            updateService.processUpdate(update);
            log.info("Update processed: {}", update);
        } catch (Exception e) {
            log.info("An error occurred while processing update. Sending to DLQ.");
            dlqProducer.send(update);
        }
    }
}
