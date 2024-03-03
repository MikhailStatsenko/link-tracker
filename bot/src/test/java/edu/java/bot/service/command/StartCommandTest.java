package edu.java.bot.service.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class StartCommandTest extends CommandTest {

    private StartCommand startCommand;

    @BeforeEach
    public void setup() {
        super.setUp();
        startCommand = new StartCommand(scrapperClient);
    }

    @Test
    void testCommand() {
        assertThat(startCommand.command()).isEqualTo("/start");
    }

    @Test
    void testDescription() {
        assertThat(startCommand.description()).isEqualTo("Начать работу с ботом");
    }

    @Test
    public void testHandleWhenUserNotRegistered() {
        scrapperClient.registerChat(CHAT_ID);

        String expectedText = "Добро пожаловать!\nЧтобы увидеть список команд, используйте /help";
        SendMessage actual = startCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }

    @Test
    public void testHandleWhenUserAlreadyRegistered() {
        when(scrapperClient.registerChat(CHAT_ID)).thenThrow(RuntimeException.class);

        String expectedText = "Бот уже запущен";
        SendMessage actual = startCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }
}
