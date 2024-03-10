package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.dto.response.ListLinksResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListCommand implements Command {
    private static final String COMMAND = "/list";
    private static final String DESCRIPTION = "Просмотреть список отслеживаемых ссылок";

    private static final String TRACKED_LINKS_HEADER =  "Отслеживаемые ресурсы:\n";
    private static final String NO_LINKS_TRACKED = "Вы пока не отслеживаете никакие ссылки!\n"
        + "Чтобы добавить новую ссылку, используйте /track";

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
        ListLinksResponse response = scrapperClient.getLinks(chatId);
        if (response.size() == 0) {
            return new SendMessage(chatId, NO_LINKS_TRACKED);
        }

        StringBuilder links = new StringBuilder();
        response.links().forEach(x -> links.append("- ").append(x.url()).append("\n"));

        return new SendMessage(chatId, TRACKED_LINKS_HEADER + links).disableWebPagePreview(true);
    }
}
