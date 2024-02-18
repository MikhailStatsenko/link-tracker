package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import edu.java.bot.service.command.RequiresTextInput;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMessageProcessorImpl implements UserMessageProcessor {
    private Command currentCommand;
    private final List<? extends Command> commands;

    private static final String ONLY_TEXT_COMMANDS_SUPPORTED = "Бот работает только с текстовыми сообщениями";
    private static final String UNSUPPORTED_COMMAND = "Такая команда не поддерживается. Список команд: /help";

    @Override
    public List<? extends Command> commands() {
        return commands;
    }

    @Override
    public SendMessage process(Update update) {
        SendMessage message = null;

        if (!isTextMessage(update)) {
            message = new SendMessage(update.message().chat().id(), ONLY_TEXT_COMMANDS_SUPPORTED);
        } else if (isCommand(update)) {
            message = processCommand(update);
        } else if (isInputExpected()) {
            message = processInput(update);
        }

        return message != null ? message : new SendMessage(update.message().chat().id(), UNSUPPORTED_COMMAND);
    }

    private boolean isTextMessage(Update update) {
        return update.message().text() != null;
    }

    private boolean isCommand(Update update) {
        String text = update.message().text();
        return text != null && text.strip().startsWith("/");
    }

    private boolean isInputExpected() {
        return currentCommand instanceof RequiresTextInput;
    }

    private SendMessage processCommand(Update update) {
        SendMessage msg = null;
        for (Command command : commands) {
            if (command.supports(update)) {
                msg = command.handle(update);
                currentCommand = command;
                break;
            }
        }
        return msg;
    }

    private SendMessage processInput(Update update) {
        var message = currentCommand.handle(update);
        currentCommand = null;
        return message;
    }
}
