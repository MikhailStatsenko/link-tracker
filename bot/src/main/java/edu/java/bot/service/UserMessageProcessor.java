package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMessageProcessor {
    private static final String ERROR_MESSAGE = "Во время работы бота возникла ошибка, попробуйте его перезапустить";
    private static final String ONLY_TEXT_COMMANDS_SUPPORTED = "Бот работает только с текстовыми сообщениями";
    private static final String UNSUPPORTED_COMMAND = "Такая команда не поддерживается. Список команд: /help";

    private final List<? extends Command> commands;


    public List<? extends Command> commands() {
        return commands;
    }

    public SendMessage process(Update update) {
        long chatId = update.message().chat().id();

        if (!isTextMessage(update)) {
            return new SendMessage(chatId, ONLY_TEXT_COMMANDS_SUPPORTED);
        }

        try {
            for (Command command : commands) {
                if (command.supports(update)) {
                    return command.handle(update);
                }
            }
        } catch (Exception e) {
            log.error("Error while handling command", e);
            return new SendMessage(chatId, ERROR_MESSAGE);
        }
        return new SendMessage(chatId, UNSUPPORTED_COMMAND);
    }

    private boolean isTextMessage(Update update) {
        String text = update.message().text();
        return text != null && !text.isEmpty();
    }
}
