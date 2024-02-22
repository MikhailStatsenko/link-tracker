package edu.java.bot.service.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class HelpCommandTest extends CommandTest {

    private HelpCommand helpCommand;

    @BeforeEach
    public void setup() {
        super.setUp();
        helpCommand = new HelpCommand();
    }

    @Test
    void testCommand() {
        assertThat(helpCommand.command()).isEqualTo("/help");
    }

    @Test
    void testDescription() {
        assertThat(helpCommand.description()).isEqualTo("Доступные команды");
    }

    @Test
    public void testHandleHelpCommand() {
        String expectedText = "Этот бот позволяет получать уведомления от различных ресурсов в интернете.\n" +
            "В данный момент поддерживаются уведомления от GitHub и StackOverflow\n\n" +
            "Ботом можно управлять с помощью следующих команд:\n\n" +
            "/start - начать работу с ботом\n" +
            "/help - получить доступные команды бота\n" +
            "/track <ссылка> - начать отслеживать ресурс\n" +
            "/untrack <ссылка> - удалить ресурс из отслеживаемых\n" +
            "/list - получить список отслеживаемых ресурсов";
        SendMessage actual = helpCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }
}
