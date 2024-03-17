package edu.java.bot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserMessageProcessorTest {

    private UserMessageProcessor userMessageProcessor;

    @Mock
    private Command mockCommand1;
    @Mock
    private Command mockCommand2;

    @Mock
    protected Update mockUpdate;

    @Mock
    protected Message mockMessage;

    @Mock
    protected Chat mockChat;

    private static final long CHAT_ID = 123L;

    @BeforeEach
    public void setUp() {
        List<Command> commands = new ArrayList<>();
        commands.add(mockCommand1);
        commands.add(mockCommand2);
        userMessageProcessor = new UserMessageProcessor(commands);

        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);
    }

    @Test
    public void testCommands() {
        assertThat(userMessageProcessor.commands()).isEqualTo(List.of(mockCommand1, mockCommand2));
    }

    @Test
    public void testProcessWhenTextMessageAndSupportedCommand() {
        when(mockChat.id()).thenReturn(CHAT_ID);
        when(mockUpdate.message().text()).thenReturn("/command1");

        when(mockCommand1.supports(mockUpdate)).thenReturn(true);
        when(mockCommand1.handle(mockUpdate)).thenReturn(new SendMessage(CHAT_ID, "Команда успешно обработана"));

        SendMessage result = userMessageProcessor.process(mockUpdate);
        String expectedText = "Команда успешно обработана";

        assertThat(result.getParameters().get("text")).isEqualTo(expectedText);
    }

    @Test
    public void testProcessWhenTextMessageAndNoSupportedCommand() {
        when(mockMessage.text()).thenReturn("Hello World!");

        SendMessage result = userMessageProcessor.process(mockUpdate);
        String expectedText = "Такая команда не поддерживается. Список команд: /help";

        assertThat(result.getParameters().get("text")).isEqualTo(expectedText);
    }

    @Test
    public void testProcessWithNotTextMessage() {
        when(mockMessage.text()).thenReturn(null);

        SendMessage result = userMessageProcessor.process(mockUpdate);
        String expectedText = "Бот работает только с текстовыми сообщениями";

        assertThat(result.getParameters().get("text")).isEqualTo(expectedText);
    }
}
