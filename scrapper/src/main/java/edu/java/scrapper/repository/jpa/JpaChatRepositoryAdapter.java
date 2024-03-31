package edu.java.scrapper.repository.jpa;

import edu.java.scrapper.model.Chat;
import edu.java.scrapper.repository.ChatRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaChatRepositoryAdapter implements ChatRepository {
    private final JpaChatRepository chatRepository;

    @Override
    public List<Chat> findAll() {
        return chatRepository.findAll();
    }

    @Override
    public List<Long> findAllChatIdsByLinkId(long linkId) {
        return chatRepository.findAllChatIdsByLinkId(linkId);
    }

    @Override
    public Optional<Chat> findById(long chatId) {
        return chatRepository.findById(chatId);
    }

    @Override
    public void save(Chat chat) {
        chatRepository.save(chat);
    }

    @Override
    public int delete(long chatId) {
        return chatRepository.deleteChatById(chatId);
    }
}
