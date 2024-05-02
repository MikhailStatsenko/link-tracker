package edu.java.scrapper.service.update;

import edu.java.scrapper.dto.bot.LinkUpdateRequest;

public interface UpdateSender {
    void sendUpdates(LinkUpdateRequest request);
}
