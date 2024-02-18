package edu.java.bot.service.command;

import edu.java.bot.model.User;
import edu.java.bot.service.parser.Handler;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TrackCommandTest extends CommandTest {

    private TrackCommand trackCommand;

    @Mock
    private User mockUser;

    @Mock
    private Handler mockHandler;

    @BeforeEach
    public void setup() {
        trackCommand = new TrackCommand(mockUserRepository, List.of(mockHandler));
        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(CHAT_ID);
    }

    @Test
    public void testHandleWithRequestForLinkEntry() {
        assertMessageTextEquals("Введите ссылку для отслеживания:", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWithInvalidLink() {
        when(mockMessage.text()).thenReturn("github");
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(mockUser));

        assertMessageTextEquals("Неверный формат ссылки", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWithUnsupportedResource() {
        when(mockMessage.text()).thenReturn("http://example.com");
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(mockUser));
        when(mockHandler.supports(any())).thenReturn(false);

        assertMessageTextEquals("Данный ресурс не поддерживается!", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAlreadyTracked() {
        Set<String> trackedLinks = new HashSet<>();
        trackedLinks.add("http://example.com");

        when(mockMessage.text()).thenReturn("http://example.com");
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(mockUser));
        when(mockUser.getLinks()).thenReturn(trackedLinks);
        when(mockHandler.supports(any())).thenReturn(true);

        assertMessageTextEquals("Данный ресурс уже отслеживается", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAddedSuccessfully() {
        Set<String> trackedLinks = new HashSet<>();

        when(mockMessage.text()).thenReturn("http://example.com");
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(mockUser));
        when(mockUser.getLinks()).thenReturn(trackedLinks);
        when(mockHandler.supports(any())).thenReturn(true);

        assertMessageTextEquals("Ссылка добавлена!", trackCommand.handle(mockUpdate));
        assertThat(trackedLinks).contains("http://example.com");
    }
}

