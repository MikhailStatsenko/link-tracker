package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommandTest {
    @Mock
    protected UserRepository mockUserRepository;

    @Mock
    protected Update mockUpdate;

    @Mock
    protected Message mockMessage;

    @Mock
    protected Chat mockChat;

    protected static final long CHAT_ID = 123L;

    protected void assertMessageTextEquals(String expectedText, SendMessage actualMessage) {
        long actualChatId = (long) actualMessage.getParameters().get("chat_id");
        String actualText = (String) actualMessage.getParameters().get("text");

        assertThat(actualChatId).isEqualTo(CHAT_ID);
        assertThat(actualText).isEqualTo(expectedText);
    }
}
