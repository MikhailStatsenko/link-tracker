package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.User;
import edu.java.bot.repository.UserRepository;
import edu.java.bot.service.parser.Handler;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackCommand implements Command, RequiresTextInput {
    private final UserRepository userRepository;
    private final List<? extends Handler> handlers;

    private static final String COMMAND = "/track";
    private static final String DESCRIPTION = "Начать отслеживание ссылки";

    private static final String REQUEST_LINK_ENTRY = "Введите ссылку для отслеживания:";
    private static final String USER_NOT_FOUND = "Пользователь не найден.\n"
        + "Перезапустите бота с помощью /start";
    private static final String LINK_ADDED_SUCCESSFULLY = "Ссылка добавлена!";
    private static final String LINK_ALREADY_TRACKED = "Данный ресурс уже отслеживается";
    private static final String WRONG_LINK_FORMAT = "Неверный формат ссылки";
    private static final String UNSUPPORTED_RESOURCE = "Данный ресурс не поддерживается!";

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
        if (!isInputGiven(update)) {
            return new SendMessage(update.message().chat().id(), REQUEST_LINK_ENTRY);
        }
        return handleInput(update);
    }

    private SendMessage handleInput(Update update) {
        long chatId = update.message().chat().id();
        String link = update.message().text();

        Optional<User> userOptional = userRepository.findByChatId(chatId);
        if (userOptional.isEmpty()) {
            return new SendMessage(chatId, USER_NOT_FOUND);
        }

        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            return new SendMessage(chatId, WRONG_LINK_FORMAT);
        }

        URI uri = URI.create(link);
        for (Handler linkHandler : handlers) {
            if (linkHandler.supports(uri)) {
                boolean isLinkNew = userOptional.get().getLinks().add(link);
                return new SendMessage(chatId, isLinkNew ? LINK_ADDED_SUCCESSFULLY : LINK_ALREADY_TRACKED);
            }
        }
        return new SendMessage(chatId, UNSUPPORTED_RESOURCE);
    }
}
