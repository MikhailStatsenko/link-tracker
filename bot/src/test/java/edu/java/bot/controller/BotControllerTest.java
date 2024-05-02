package edu.java.bot.controller;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.UpdateService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BotControllerTest {

    @Mock
    private UpdateService updateService;

    @InjectMocks
    private BotController botController;

    @Test
    void testProcessUpdate_Success() {
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            1L,
            URI.create("http://example.com"),
            "Description",
            List.of(123L)
        );

        ResponseEntity<Void> responseEntity = botController.processUpdate(linkUpdateRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(updateService, times(1)).processUpdate(linkUpdateRequest);
    }
}
