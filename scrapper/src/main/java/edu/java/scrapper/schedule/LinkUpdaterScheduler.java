package edu.java.scrapper.schedule;

import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.service.update.UpdateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkUpdaterScheduler {
    @Value("${api.bot.update.interval-ms}")
    private long interval;

    private final BotClient botClient;
    private final UpdateService updateService;

    @Scheduled(fixedDelayString = "#{@linksScheduler.interval()}")
    public void update() {
        log.info("Looking for updates");

        List<LinkUpdateRequest> updateRequests = updateService.fetchAllUpdates(interval);
        for (var updateRequest : updateRequests) {
            botClient.sendUpdates(updateRequest);
        }
        log.info("All links are up to date");
    }
}
