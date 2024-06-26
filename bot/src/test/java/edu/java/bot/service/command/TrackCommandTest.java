package edu.java.bot.service.command;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.exception.ApiBadRequestException;
import edu.java.bot.service.parser.Parser;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TrackCommandTest extends CommandTest {
    private TrackCommand trackCommand;

    @Mock
    private Parser mockParser;

    @BeforeEach
    public void setup() {
        super.setUp();
        trackCommand = new TrackCommand(scrapperClient, List.of(mockParser));
    }

    @Test
    void testCommand() {
        assertThat(trackCommand.command()).isEqualTo("/track");
    }

    @Test
    void testDescription() {
        assertThat(trackCommand.description()).isEqualTo("Начать отслеживание ссылки");
    }

    @Test
    public void testHandleWhenLinkNotSent() {
        when(mockMessage.text()).thenReturn("/track");

        String expectedText = "Введите ссылку для отслеживания сразу после команды"
            + "\nНапример: /track https://github.com/user/example/";

        assertMessageTextEquals(expectedText, trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWithInvalidLink() {
        when(mockMessage.text()).thenReturn("/track github");

        assertMessageTextEquals("Неверный формат ссылки", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWithUnsupportedResource() {
        when(mockMessage.text()).thenReturn("/track http://example.com");

        assertMessageTextEquals("Данный ресурс не поддерживается!", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAlreadyTracked() {
        when(mockMessage.text()).thenReturn("/tack http://example.com");
        when(scrapperClient.addLink(
            CHAT_ID,
            new AddLinkRequest(URI.create("http://example.com")))
        ).thenThrow(new ApiBadRequestException(mockErrorResponse));
        when(mockParser.supports(any())).thenReturn(true);

        assertMessageTextEquals("Данный ресурс уже отслеживается", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAddedSuccessfully() {
        URI url = URI.create("http://example.com");
        when(mockMessage.text()).thenReturn("/track http://example.com");
        when(mockParser.supports(any())).thenReturn(true);
        when(scrapperClient.addLink(CHAT_ID, new AddLinkRequest(url))).thenReturn(new LinkResponse(1L, url));

        assertMessageTextEquals("Ссылка добавлена!", trackCommand.handle(mockUpdate));
    }
}

