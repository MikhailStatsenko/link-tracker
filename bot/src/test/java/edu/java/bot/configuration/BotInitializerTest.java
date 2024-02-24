package edu.java.bot.configuration;

import edu.java.bot.service.LinkTrackerBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BotInitializerTest {

    @Mock
    private LinkTrackerBot mockBot;

    @Test
    public void testInit() {
        BotInitializer botInitializer = new BotInitializer(mockBot);

        botInitializer.init();

        verify(mockBot).start();
    }
}
