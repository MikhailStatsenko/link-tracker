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
public class ListCommand implements Command {
    private final UserRepository userRepository;
    private static final String COMMAND = "/list";
    private static final String DESCRIPTION = "Просмотреть список отслеживаемых ссылок";

    private static final String TRACKED_LINKS_HEADER =  "Отслеживаемые ресурсы:\n";
    private static final String USER_NOT_FOUND = "Пользователь не найден.\n"
        + "Перезапустите бота с помощью /start";
    private static final String NO_LINKS_TRACKED = "Вы пока не отслеживаете никакие ссылки!\n"
        + "Чтобы добавить новую ссылку, используйте /track";

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
        Optional<User> userOptional =  userRepository.findByChatId(chatId);

        if (userOptional.isEmpty()) {
            return new SendMessage(chatId, USER_NOT_FOUND);
        }
        StringBuilder links = new StringBuilder();
        userOptional.get().getLinks().forEach(x -> links.append("- ").append(x).append("\n"));

        return new SendMessage(chatId, links.isEmpty() ? NO_LINKS_TRACKED : TRACKED_LINKS_HEADER + links)
            .disableWebPagePreview(true);
    }
}
