package edu.java.scrapper.exception.handler;

import edu.java.scrapper.dto.api.response.ApiErrorResponse;
import edu.java.scrapper.exception.LinkAlreadyTrackedException;
import edu.java.scrapper.exception.LinkNotFoundException;
import edu.java.scrapper.exception.UserAlreadyExistsException;
import edu.java.scrapper.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

public class ScrapperControllerExceptionHandlerTest {

    private final ScrapperControllerExceptionHandler exceptionHandler = new ScrapperControllerExceptionHandler();

    @Test
    public void testHandleLinkNotFoundException() {
        LinkNotFoundException exception = new LinkNotFoundException();

        ResponseEntity<ApiErrorResponse> responseEntity = exceptionHandler.handleLinkNotFoundException(exception);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().description()).isEqualTo("Ссылка не найдена");
    }

    @Test
    public void testHandleUserNotFoundException() {
        UserNotFoundException exception = new UserNotFoundException();

        ResponseEntity<ApiErrorResponse> responseEntity = exceptionHandler.handleUserNotFoundException(exception);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().description()).isEqualTo("Пользователь не найден");
    }

    @Test
    public void testHandleLinkAlreadyTrackedException() {
        LinkAlreadyTrackedException exception = new LinkAlreadyTrackedException();

        ResponseEntity<ApiErrorResponse> responseEntity = exceptionHandler.handleLinkAlreadyTrackedException(exception);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().description()).isEqualTo("Ссылка уже отслеживается");
    }

    @Test
    public void testHandleUserAlreadyExistsException() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException();

        ResponseEntity<ApiErrorResponse> responseEntity = exceptionHandler.handleUserAlreadyExistsException(exception);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().description()).isEqualTo("Пользователь уже существует");
    }

    @Test
    public void testHandleGenericException() {
        Exception exception = new Exception("Some generic exception");

        ResponseEntity<?> responseEntity = exceptionHandler.handle(exception);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isInstanceOf(ApiErrorResponse.class);
    }
}

