package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

class BotControllerExceptionHandlerTest {
    BotControllerExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new BotControllerExceptionHandler();
    }

    @Test
    void testHandleException() {
        Exception exception = new RuntimeException("Test exception");

        ResponseEntity<?> responseEntity = handler.handle(exception);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((ApiErrorResponse) responseEntity.getBody()).description()).isEqualTo("Test exception");
        assertThat(((ApiErrorResponse) responseEntity.getBody()).code()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(((ApiErrorResponse) responseEntity.getBody()).exceptionName()).isEqualTo("RuntimeException");
        assertThat(((ApiErrorResponse) responseEntity.getBody()).exceptionMessage()).isEqualTo("Test exception");
    }
}
