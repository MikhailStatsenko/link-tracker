package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.exception.UserAlreadyExistsException;
import edu.java.scrapper.exception.UserNotFoundException;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.repository.JdbcChatRepository;
import edu.java.scrapper.service.ChatService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {

    private final JdbcChatRepository chatRepository;

    @Override
    public void register(Long id) {
        var user = chatRepository.findById(id);
        if (user.isPresent()) {
            throw new UserAlreadyExistsException();
        }
        chatRepository.save(new Chat(id, new ArrayList<>()));
    }

    @Override
    public void unregister(Long id) {
        boolean userDeleted = chatRepository.delete(id) > 0;
        if (!userDeleted) {
            throw new UserNotFoundException();
        }
    }

    @Override
    public List<Long> listAllChatIdsByLinkId(long linkId) {
        return chatRepository.findAllChatIdsByLinkId(linkId);
    }
}
