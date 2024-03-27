package edu.java.bot.service;

import static org.junit.jupiter.api.Assertions.*;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dto.request.LinkUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UpdateServiceTest {

    @Mock
    private LinkTrackerBot mockBot;

    @InjectMocks
    private UpdateService updateService;

    @Test
    public void testProcessUpdate() {
        LinkUpdate update = new LinkUpdate(1L, URI.create("http://example.com"), "Description", List.of(1L, 2L));

        updateService.processUpdate(update);

        verify(mockBot, times(2)).execute(any(SendMessage.class));
    }
}
