package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.User;
import edu.java.bot.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UntrackCommand implements Command {
    private static final String COMMAND = "/untrack";
    private static final String DESCRIPTION = "Прекратить отслеживание ссылки";

    private static final String LINK_NOT_SENT = "Введите ссылку от которой хотите отказаться сразу после команды"
        + "\nНапример: /untrack https://github.com/user/example/";
    private static final String USER_NOT_FOUND = "Пользователь не найден.\n"
        + "Перезапустите бота с помощью /start";
    private static final String LINK_SUCCESSFULLY_REMOVED = "Ссылка удалена из отслеживаемых";
    private static final String NO_SUCH_TRACKED_LINK = "Такого ресурса нет среди отслеживаемых";

    private final UserRepository userRepository;

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
        String[] messageTextParts = update.message().text().split(" +", 2);

        if (messageTextParts.length < 2) {
            return new SendMessage(chatId, LINK_NOT_SENT);
        }

        String link = messageTextParts[1];
        Optional<User> userOptional = userRepository.findByChatId(chatId);

        if (userOptional.isEmpty()) {
            return new SendMessage(chatId, USER_NOT_FOUND);
        }

        boolean isLinkTraced = userOptional.get().getLinks().remove(link);
        return new SendMessage(chatId, isLinkTraced ? LINK_SUCCESSFULLY_REMOVED : NO_SUCH_TRACKED_LINK);
    }
}
