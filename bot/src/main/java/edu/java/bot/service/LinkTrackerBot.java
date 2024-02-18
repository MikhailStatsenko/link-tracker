package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.command.Command;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkTrackerBot implements Bot {
    private TelegramBot bot;
    private final ApplicationConfig config;
    private final UserMessageProcessor userMessageProcessor;

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        try {
            bot.execute(request);
            log.info("Bot request \"{}\" successfully executed. Chat_id: {}",
                request.getMethod(), request.getParameters().get("chat_id"));
        } catch (Exception e) {
            log.error("Error executing bot request: {}", e.getMessage(), e);
        }
    }

    @Override
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

    @Override
    public void start() {
        bot = new TelegramBot(config.telegramToken());

        bot.execute(new SetMyCommands(userMessageProcessor
            .commands()
            .stream()
            .map(Command::toApiCommand)
            .toArray(BotCommand[]::new)));

        bot.setUpdatesListener(this::process);
        log.info("Bot started successfully");
    }

    @Override
    public void close() {
        bot.removeGetUpdatesListener();
        log.info("Bot updates listener removed");
    }
}
