package edu.java.scrapper.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LinkUpdaterScheduler {
    @Scheduled(fixedDelayString = "#{@linksScheduler.interval()}")
    public void update() {
        log.info("Looking for updates");
        // some logic
        log.info("All links are up to date");
    }
}
