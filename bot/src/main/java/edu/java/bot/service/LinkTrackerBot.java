package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.command.Command;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LinkTrackerBot extends TelegramBot implements AutoCloseable {
    private final UserMessageProcessor userMessageProcessor;

    @Autowired
    public LinkTrackerBot(ApplicationConfig config, UserMessageProcessor userMessageProcessor) {
        super(config.telegramToken());
        this.userMessageProcessor = userMessageProcessor;
    }

    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                SendMessage msg = userMessageProcessor.process(update);
                execute(msg);
            } catch (Exception e) {
                log.error("Error processing update: {}", e.getMessage(), e);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void start() {
        setCommands();
        setUpdatesListener(this::process);
        log.info("Bot started successfully");
    }

    @Override
    public void close() {
        removeGetUpdatesListener();
        log.info("Bot updates listener removed");
    }

    private void setCommands() {
        execute(new SetMyCommands(userMessageProcessor
            .commands()
            .stream()
            .map(Command::toApiCommand)
            .toArray(BotCommand[]::new)));
    }
}
