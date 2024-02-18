package edu.java.bot.service.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;


public class HelpCommandTest extends CommandTest {

    private HelpCommand helpCommand;

    @BeforeEach
    public void setup() {
        helpCommand = new HelpCommand();
        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(CHAT_ID);
    }

    @Test
    public void testHandleHelpCommand() {
        String expectedText = "Этот бот позволяет получать уведомления от различных ресурсов в интернете.\n" +
            "В данный момент поддерживаются уведомления от GitHub и StackOverflow\n\n" +
            "Ботом можно управлять с помощью следующих команд:\n\n" +
            "/start - начать работу с ботом\n" +
            "/help - получить доступные команды бота\n" +
            "/track - начать отслеживать ресурс\n" +
            "/untrack - удалить ресурс из отслеживаемых\n" +
            "/list - получить список отслеживаемых ресурсов";
        SendMessage actual = helpCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }
}
