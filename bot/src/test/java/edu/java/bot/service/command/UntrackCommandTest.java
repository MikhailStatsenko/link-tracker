package edu.java.bot.service.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.exception.ApiBadRequestException;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class UntrackCommandTest extends CommandTest {

    private UntrackCommand untrackCommand;

    @BeforeEach
    public void setup() {
        super.setUp();
        untrackCommand = new UntrackCommand(scrapperClient);
    }

    @Test
    void testCommand() {
        assertThat(untrackCommand.command()).isEqualTo("/untrack");
    }

    @Test
    void testDescription() {
        assertThat(untrackCommand.description()).isEqualTo("Прекратить отслеживание ссылки");
    }

    @Test
    public void testHandleWhenLinkNotSent() {
        when(mockMessage.text()).thenReturn("/untrack");

        String expectedText = "Введите ссылку от которой хотите отказаться сразу после команды"
            + "\nНапример: /untrack https://github.com/user/example/";

        assertMessageTextEquals(expectedText, untrackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkNotTracked() {
        when(mockMessage.text()).thenReturn("/untrack https://example.com");
        when(scrapperClient.deleteLink(
            CHAT_ID,
            new RemoveLinkRequest(URI.create("https://example.com")))
        ).thenThrow(new ApiBadRequestException(mockErrorResponse));

        String expectedText = "Такого ресурса нет среди отслеживаемых";
        SendMessage actual = untrackCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }

    @Test
    public void testHandleWhenLinkRemovedSuccessfully() {
        when(mockMessage.text()).thenReturn("/untrack https://example.com");
        when(scrapperClient.deleteLink(
            CHAT_ID,
            new RemoveLinkRequest(URI.create("https://example.com")))
        ).thenReturn(new LinkResponse(
            1L, URI.create("https://example.com")))
        ;

        String expectedText = "Ссылка удалена из отслеживаемых";
        SendMessage actual = untrackCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }
}
