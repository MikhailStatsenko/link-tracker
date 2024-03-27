package edu.java.scrapper.repository;

import edu.java.scrapper.model.Chat;
import java.util.List;
import java.util.Optional;

public interface ChatRepository {
    List<Chat> findAll();

    List<Long> findAllChatIdsByLinkId(long linkId);

    Optional<Chat> findById(long chatId);

    void save(Chat chat);

    int delete(long chatId);
}
