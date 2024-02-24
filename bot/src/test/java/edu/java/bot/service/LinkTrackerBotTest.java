package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkTrackerBotTest {
    @Mock
    private UserMessageProcessor userMessageProcessor;

    @Mock
    private ApplicationConfig applicationConfig;

    private LinkTrackerBot linkTrackerBot;

    @BeforeEach
    public void setUp() {
        linkTrackerBot = spy(new LinkTrackerBot(applicationConfig, userMessageProcessor));
    }

    @Test
    public void testProcess() {
        Update update = new Update();
        SendMessage sendMessage = new SendMessage(123L, "Hello World");
        when(userMessageProcessor.process(update)).thenReturn(sendMessage);

        List<Update> updates = Collections.singletonList(update);
        linkTrackerBot.process(updates);

        verify(linkTrackerBot).execute(sendMessage);
    }

    @Test
    public void testStart() {
        doNothing().when(linkTrackerBot).setUpdatesListener(any());

        linkTrackerBot.start();

        verify(linkTrackerBot).setUpdatesListener(any());
    }

    @Test
    public void testClose() {
        doNothing().when(linkTrackerBot).removeGetUpdatesListener();
        linkTrackerBot.close();
        verify(linkTrackerBot).removeGetUpdatesListener();
    }
}
