package edu.java.bot.service.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.User;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ListCommandTest extends CommandTest{

    private ListCommand listCommand;

    @BeforeEach
    public void setup() {
        super.setUp();
        listCommand = new ListCommand(mockUserRepository);
    }

    @Test
    void testCommand() {
        assertThat(listCommand.command()).isEqualTo("/list");
    }

    @Test
    void testDescription() {
        assertThat(listCommand.description()).isEqualTo("Просмотреть список отслеживаемых ссылок");
    }

    @Test
    public void testHandleUserNotFound() {
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.empty());

        String  expectedText ="Пользователь не найден.\nПерезапустите бота с помощью /start";
        SendMessage actual = listCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }

    @Test
    public void testHandleNoLinksTracked() {
        Set<String> emptySet = Collections.emptySet();
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(new User(CHAT_ID, emptySet)));

        String expectedText = "Вы пока не отслеживаете никакие ссылки!\nЧтобы добавить новую ссылку, используйте /track";
        SendMessage actual = listCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }

    @Test
    public void testHandleWithTrackedLinks() {
        Set<String> trackedLinks = new HashSet<>(List.of("TrackedLink1", "TrackedLink2"));
        when(mockUserRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(new User(CHAT_ID, trackedLinks)));

        String expectedText = "Отслеживаемые ресурсы:\n- TrackedLink1\n- TrackedLink2\n";
        SendMessage actual = listCommand.handle(mockUpdate);
        boolean displayWebPagePreview = (boolean) actual.getParameters().get("disable_web_page_preview");

        assertMessageTextEquals(expectedText, actual);
        assertThat(displayWebPagePreview).isTrue();
    }
}
