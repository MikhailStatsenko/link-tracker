package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMessageProcessorImpl implements UserMessageProcessor {
    private static final String ONLY_TEXT_COMMANDS_SUPPORTED = "Бот работает только с текстовыми сообщениями";
    private static final String UNSUPPORTED_COMMAND = "Такая команда не поддерживается. Список команд: /help";

    private final List<? extends Command> commands;

    @Override
    public List<? extends Command> commands() {
        return commands;
    }

    @Override
    public SendMessage process(Update update) {
        long chatId = update.message().chat().id();

        if (!isTextMessage(update)) {
            return new SendMessage(chatId, ONLY_TEXT_COMMANDS_SUPPORTED);
        }

        for (Command command : commands) {
            if (command.supports(update)) {
                return command.handle(update);
            }
        }
        return new SendMessage(chatId, UNSUPPORTED_COMMAND);
    }

    private boolean isTextMessage(Update update) {
        String text = update.message().text();
        return text != null && !text.isEmpty();
    }
}
