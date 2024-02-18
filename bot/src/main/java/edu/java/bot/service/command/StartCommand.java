package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.User;
import edu.java.bot.repository.UserRepository;
import java.util.HashSet;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StartCommand implements Command {
    private final UserRepository userRepository;
    private static final String COMMAND = "/start";
    private static final String DESCRIPTION = "Начать работу с ботом";

    private static final String USER_REGISTERED = "Добро пожаловать!\n"
        + "Чтобы увидеть список команд, используйте /help";
    private static final String USER_ALREADY_EXISTS = "Бот уже запущен\n";

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
        Optional<User> user = userRepository.findByChatId(chatId);

        if (user.isEmpty()) {
            userRepository.save(new User(chatId, new HashSet<>()));
            return new SendMessage(update.message().chat().id(), USER_REGISTERED);
        }
        return new SendMessage(update.message().chat().id(), USER_ALREADY_EXISTS);
    }
}
