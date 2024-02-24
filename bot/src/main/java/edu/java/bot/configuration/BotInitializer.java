package edu.java.bot.configuration;

import edu.java.bot.service.LinkTrackerBot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotInitializer {
    private final LinkTrackerBot bot;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        bot.start();
    }
}
