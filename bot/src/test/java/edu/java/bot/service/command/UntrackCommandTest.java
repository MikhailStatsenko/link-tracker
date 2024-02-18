package edu.java.bot.service.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.User;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

public class UntrackCommandTest extends CommandTest {

    private UntrackCommand untrackCommand;

    @Mock
    private User mockUser;

    @BeforeEach
    public void setup() {
        untrackCommand = new UntrackCommand(mockUserRepository);
        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(CHAT_ID);
    }

    @Test
    public void testHandleWithRequestForLinkEntry() {
        String expectedText = "Введите ссылку, от которой вы хотите отписаться:";
        SendMessage actual = untrackCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }

    @Test
    public void testHandleWhenNoSuchLink() {
        when(mockMessage.text()).thenReturn("https://example.com");
        when(mockUserRepository.findByChatId(123L)).thenReturn(Optional.empty());

        String expectedText = "Пользователь не найден.\nПерезапустите бота с помощью /start";
        SendMessage actual = untrackCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }

    @Test
    public void testHandleWhenLinkRemovedSuccessfully() {
        when(mockMessage.text()).thenReturn("https://example.com");
        HashSet<String> trackedLinks = new HashSet<>();
        trackedLinks.add("https://example.com");
        when(mockUser.getLinks()).thenReturn(trackedLinks);
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(mockUser));

        String expectedText = "Ссылка удалена из отслеживаемых";
        SendMessage actual = untrackCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }

    @Test
    public void testHandle_LinkNotTracked() {
        when(mockMessage.text()).thenReturn("https://example.com");
        when(mockUser.getLinks()).thenReturn(new HashSet<>());
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(mockUser));

        String expectedText = "Такого ресурса нет среди отслеживаемых";
        SendMessage actual = untrackCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }
}
