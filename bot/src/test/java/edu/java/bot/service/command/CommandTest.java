package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
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
class CommandTest {
    @Mock
    protected ScrapperClient scrapperClient;

    @Mock
    protected Update mockUpdate;

    @Mock
    protected Message mockMessage;

    @Mock
    protected Chat mockChat;

    protected static final long CHAT_ID = 123L;

    @BeforeEach
    void setUp() {
        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(CHAT_ID);
    }

    protected void assertMessageTextEquals(String expectedText, SendMessage actualMessage) {
        long actualChatId = (long) actualMessage.getParameters().get("chat_id");
        String actualText = (String) actualMessage.getParameters().get("text");

        assertThat(actualChatId).isEqualTo(CHAT_ID);
        assertThat(actualText).isEqualTo(expectedText);
    }



    @Test
    public void testSupports() {
        when(mockMessage.text()).thenReturn("/test");

        Command command = new TestCommand();

        assertThat(command.supports(mockUpdate)).isTrue();
    }

    @Test
    public void testToApiCommand() {
        Command command = new TestCommand();

        BotCommand botCommand = command.toApiCommand();

        assertThat(botCommand.command()).isEqualTo("/test");
        assertThat(botCommand.description()).isEqualTo("Test command");
    }

    private static class TestCommand implements Command {
        private static final String COMMAND = "/test";
        private static final String DESCRIPTION = "Test command";

        @Override
        public String command() {
            return COMMAND;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public SendMessage handle(Update update) {
            return null; // Not needed for this test
        }
    }
}
