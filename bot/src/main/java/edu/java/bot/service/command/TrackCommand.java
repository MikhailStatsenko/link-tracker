package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.exception.ApiBadRequestException;
import edu.java.bot.service.parser.Parser;
import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackCommand implements Command {
    private static final String COMMAND = "/track";
    private static final String DESCRIPTION = "Начать отслеживание ссылки";

    private static final String LINK_NOT_SENT = "Введите ссылку для отслеживания сразу после команды"
        + "\nНапример: /track https://github.com/user/example/";
    private static final String LINK_ADDED_SUCCESSFULLY = "Ссылка добавлена!";
    private static final String LINK_ALREADY_TRACKED = "Данный ресурс уже отслеживается";
    private static final String WRONG_LINK_FORMAT = "Неверный формат ссылки";
    private static final String UNSUPPORTED_RESOURCE = "Данный ресурс не поддерживается!";

    private final ScrapperClient scrapperClient;
    private final List<? extends Parser> parsers;
    private final Pattern linkPattern = Pattern.compile(
        "^https?://([-a-zA-Z0-9+&@#%?=~_|!])+\\.([-a-zA-Z0-9+&@#%?=~_|!]){2,}(/[-a-zA-Z0-9+&@#%?=~_|!]*)*");

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

        return addLinkToTrack(chatId, link);
    }

    private SendMessage addLinkToTrack(long chatId, String link) {
        if (!linkPattern.matcher(link).matches()) {
            return new SendMessage(chatId, WRONG_LINK_FORMAT);
        }

        URI uri = URI.create(link);
        for (Parser parser : parsers) {
            if (parser.supports(uri)) {
                try {
                    scrapperClient.addLink(chatId, new AddLinkRequest(uri));
                    return new SendMessage(chatId, LINK_ADDED_SUCCESSFULLY);
                } catch (ApiBadRequestException e) {
                    return new SendMessage(chatId, LINK_ALREADY_TRACKED);
                }
            }
        }
        return new SendMessage(chatId, UNSUPPORTED_RESOURCE);
    }
}
