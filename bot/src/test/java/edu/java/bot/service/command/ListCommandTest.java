package edu.java.bot.service.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ListCommandTest extends CommandTest{

    private ListCommand listCommand;

    @BeforeEach
    public void setup() {
        super.setUp();
        listCommand = new ListCommand(scrapperClient);
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
    public void testHandleNoLinksTracked() {
        when(scrapperClient.getLinks(CHAT_ID)).thenReturn(new ListLinksResponse(Collections.emptyList(), 0));

        String expectedText = "Вы пока не отслеживаете никакие ссылки!\nЧтобы добавить новую ссылку, используйте /track";
        SendMessage actual = listCommand.handle(mockUpdate);

        assertMessageTextEquals(expectedText, actual);
    }

    @Test
    public void testHandleWithTrackedLinks() {
        List<LinkResponse> trackedLinks = new ArrayList<>(List.of(
            new LinkResponse(1L, URI.create("TrackedLink1")),
            new LinkResponse(2L, URI.create("TrackedLink2"))));
        when(scrapperClient.getLinks(CHAT_ID)).thenReturn(new ListLinksResponse(trackedLinks, 2));

        String expectedText = "Отслеживаемые ресурсы:\n- TrackedLink1\n- TrackedLink2\n";
        SendMessage actual = listCommand.handle(mockUpdate);
        boolean displayWebPagePreview = (boolean) actual.getParameters().get("disable_web_page_preview");

        assertMessageTextEquals(expectedText, actual);
        assertThat(displayWebPagePreview).isTrue();
    }
}
