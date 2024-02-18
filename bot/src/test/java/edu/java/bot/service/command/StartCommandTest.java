package edu.java.bot.service.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.User;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StartCommandTest extends CommandTest {

    private StartCommand startCommand;

    @BeforeEach
    public void setup() {
        startCommand = new StartCommand(mockUserRepository);
        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(CHAT_ID);
    }

    @Test
    public void testHandleWhenUserNotRegistered() {
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.empty());

        String expectedText = "Добро пожаловать!\nЧтобы увидеть список команд, используйте /help";
        SendMessage actual = startCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
        verify(mockUserRepository).save(new User(CHAT_ID, new HashSet<>()));
    }

    @Test
    public void testHandleWhenUserAlreadyRegistered() {
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(new User(CHAT_ID, new HashSet<>())));

        String expectedText = "Бот уже работает.\nЧтобы увидеть список команд, используйте /help";
        SendMessage actual = startCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
        verify(mockUserRepository, never()).save(any());
    }
}
