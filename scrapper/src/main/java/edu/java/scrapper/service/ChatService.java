package edu.java.scrapper.service;

import java.util.List;

public interface ChatService {
    void register(Long id);

    void unregister(Long id);

    List<Long> findAllChatIdsByLinkId(long linkId);
}
