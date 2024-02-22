package edu.java.bot.service.command;

import edu.java.bot.model.User;
import edu.java.bot.service.parser.Parser;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TrackCommandTest extends CommandTest {

    private TrackCommand trackCommand;

    @Mock
    private User mockUser;

    @Mock
    private Parser mockParser;

    @BeforeEach
    public void setup() {
        super.setUp();
        when(mockUser.getChatId()).thenReturn(CHAT_ID);
        trackCommand = new TrackCommand(mockUserRepository, List.of(mockParser));
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
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(mockUser));

        assertMessageTextEquals("Неверный формат ссылки", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWithUnsupportedResource() {
        when(mockMessage.text()).thenReturn("/track http://example.com");
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(mockUser));
        when(mockParser.supports(any())).thenReturn(false);

        assertMessageTextEquals("Данный ресурс не поддерживается!", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAlreadyTracked() {
        Set<String> trackedLinks = new HashSet<>();
        trackedLinks.add("http://example.com");

        when(mockMessage.text()).thenReturn("/tack http://example.com");
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(mockUser));
        when(mockUser.getLinks()).thenReturn(trackedLinks);
        when(mockParser.supports(any())).thenReturn(true);

        assertMessageTextEquals("Данный ресурс уже отслеживается", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAddedSuccessfully() {
        Set<String> trackedLinks = new HashSet<>();

        when(mockMessage.text()).thenReturn("/track http://example.com");
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(mockUser));
        when(mockUser.getLinks()).thenReturn(trackedLinks);
        when(mockParser.supports(any())).thenReturn(true);

        assertMessageTextEquals("Ссылка добавлена!", trackCommand.handle(mockUpdate));
        assertThat(trackedLinks).contains("http://example.com");
    }
}

