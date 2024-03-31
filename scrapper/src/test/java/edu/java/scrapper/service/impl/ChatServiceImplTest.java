package edu.java.scrapper.service.impl;

import edu.java.scrapper.exception.UserAlreadyExistsException;
import edu.java.scrapper.exception.UserNotFoundException;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.repository.jdbc.JdbcChatRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

    @Mock
    private JdbcChatRepository chatRepository;

    @InjectMocks
    private ChatServiceImpl chatService;

    private final Long userId = 123L;
    private final Long linkId = 456L;

    @Test
    void testRegister() {
        when(chatRepository.findById(userId))
            .thenReturn(Optional.empty());

        chatService.register(userId);

        verify(chatRepository).save(any(Chat.class));
    }

    @Test
    void testRegisterWhenUserAlreadyExists() {
        when(chatRepository.findById(userId))
            .thenReturn(Optional.of(new Chat(userId, new ArrayList<>())));

        assertThrows(UserAlreadyExistsException.class, () -> chatService.register(userId));
    }

    @Test
    void testUnregister() {
        when(chatRepository.delete(userId)).thenReturn(1);

        chatService.unregister(userId);

        verify(chatRepository).delete(userId);
    }

    @Test
    void testUnregister_NonExistingUser_ThrowsUserNotFoundException() {
        when(chatRepository.delete(userId)).thenReturn(0);

        assertThrows(UserNotFoundException.class, () -> chatService.unregister(userId));
    }

    @Test
    void testListAllChatIdsByLinkId_ReturnsFindOfChatIds() {
        List<Long> expectedChatIds = List.of(1L, 2L, 3L);
        when(chatRepository.findAllChatIdsByLinkId(linkId)).thenReturn(expectedChatIds);

        List<Long> actualChatIds = chatService.findAllChatIdsByLinkId(linkId);

        assertThat(actualChatIds).isEqualTo(expectedChatIds);
    }
}

