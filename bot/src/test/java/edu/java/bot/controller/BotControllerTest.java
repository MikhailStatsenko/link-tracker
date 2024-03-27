package edu.java.bot.controller;

import edu.java.bot.dto.request.LinkUpdate;
import edu.java.bot.service.UpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BotControllerTest {

    @Mock
    private UpdateService updateService;

    @InjectMocks
    private BotController botController;

    @Test
    void testProcessUpdate_Success() {
        LinkUpdate linkUpdate = new LinkUpdate(
            1L,
            URI.create("http://example.com"),
            "Description",
            List.of(123L)
        );

        ResponseEntity<Void> responseEntity = botController.processUpdate(linkUpdate);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(updateService, times(1)).processUpdate(linkUpdate);
    }
}
