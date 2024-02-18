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
public class UntrackCommand implements Command, RequiresTextInput {
    private final UserRepository userRepository;
    private static final String COMMAND = "/untrack";
    private static final String DESCRIPTION = "Прекратить отслеживание ссылки";

    private static final String REQUEST_LINK_ENTRY = "Введите ссылку, от которой вы хотите отписаться:";
    private static final String USER_NOT_FOUND = "Пользователь не найден.\n"
        + "Перезапустите бота с помощью /start";
    private static final String LINK_SUCCESSFULLY_REMOVED = "Ссылка удалена из отслеживаемых";
    private static final String NO_SUCH_TRACKED_LINK = "Такого ресурса нет среди отслеживаемых";


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
        Optional<User> userOptional = userRepository.findByChatId(chatId);

        if (userOptional.isEmpty()) {
            return new SendMessage(chatId, USER_NOT_FOUND);
        }

        boolean isLinkTraced = userOptional.get().getLinks().remove(update.message().text());
        return new SendMessage(chatId, isLinkTraced ? LINK_SUCCESSFULLY_REMOVED : NO_SUCH_TRACKED_LINK);
    }
}
