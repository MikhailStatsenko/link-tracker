package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;

@Service
public class HelpCommand implements Command {
    private static final String COMMAND = "/help";
    private static final String DESCRIPTION = "Доступные команды";
    private static final String HELP_ANSWER =
        "Этот бот позволяет получать уведомления от различных ресурсов в интернете.\n"
            + "В данный момент поддерживаются уведомления от GitHub и StackOverflow\n\n"
            + "Ботом можно управлять с помощью следующих команд:\n\n"
            + "/start - начать работу с ботом\n"
            + "/help - получить доступные команды бота\n"
            + "/track - начать отслеживать ресурс\n"
            + "/untrack - удалить ресурс из отслеживаемых\n"
            + "/list - получить список отслеживаемых ресурсов";

    @Override
    public String command() {
        return COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        return new SendMessage(update.message().chat().id(), HELP_ANSWER);
    }
}
