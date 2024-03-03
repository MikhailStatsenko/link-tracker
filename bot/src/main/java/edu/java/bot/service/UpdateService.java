package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dto.request.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private final LinkTrackerBot bot;

    public void processUpdate(LinkUpdate update) {
        for (long chatId : update.tgChatIds()) {
            bot.execute(new SendMessage(chatId, "Произошли обновления на ресурсе: "
                + update.url() + "\n"
                + update.description()));
        }
    }
}
