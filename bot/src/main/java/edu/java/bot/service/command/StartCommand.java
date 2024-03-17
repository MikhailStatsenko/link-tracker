package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.exception.ApiBadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartCommand implements Command {
    private static final String COMMAND = "/start";
    private static final String DESCRIPTION = "Начать работу с ботом";

    private static final String USER_REGISTERED = "Добро пожаловать!\n"
        + "Чтобы увидеть список команд, используйте /help";
    private static final String USER_ALREADY_EXISTS = "Бот уже запущен";

    private final ScrapperClient scrapperClient;

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
        long chatId = update.message().chat().id();
        try {
            scrapperClient.registerChat(chatId);
            return new SendMessage(chatId, USER_REGISTERED);
        } catch (ApiBadRequestException e) {
            return new SendMessage(chatId, USER_ALREADY_EXISTS);
        }
    }
}
