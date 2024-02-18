package edu.java.bot.configuration;

import edu.java.bot.service.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotInitializer {
    private final Bot bot;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        bot.start();
    }
}
