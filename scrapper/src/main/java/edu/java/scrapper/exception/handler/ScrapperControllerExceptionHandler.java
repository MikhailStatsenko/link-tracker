package edu.java.scrapper.exception.handler;

import edu.java.scrapper.dto.api.response.ApiErrorResponse;
import edu.java.scrapper.exception.LinkAlreadyTrackedException;
import edu.java.scrapper.exception.LinkNotFoundException;
import edu.java.scrapper.exception.UserAlreadyExistsException;
import edu.java.scrapper.exception.UserNotFoundException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScrapperControllerExceptionHandler {
    private ApiErrorResponse getErrorResponse(Throwable exception, String description, HttpStatus status) {
        return new ApiErrorResponse(
            description,
            status.toString(),
            exception.getClass().getSimpleName(),
            exception.getMessage(),
            Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList())
        );
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleLinkNotFoundException(LinkNotFoundException e) {
        return new ResponseEntity<>(
            getErrorResponse(e, "Ссылка не найдена", HttpStatus.NOT_FOUND),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(
            getErrorResponse(e, "Пользователь не найден", HttpStatus.NOT_FOUND),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(LinkAlreadyTrackedException.class)
    public ResponseEntity<ApiErrorResponse> handleLinkAlreadyTrackedException(LinkAlreadyTrackedException e) {
        return new ResponseEntity<>(
            getErrorResponse(e, "Ссылка уже отслеживается", HttpStatus.BAD_REQUEST),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExistsException(LinkAlreadyTrackedException e) {
        return new ResponseEntity<>(
            getErrorResponse(e, "Пользователь уже существует", HttpStatus.BAD_REQUEST),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) {
        return new ResponseEntity<>(
            getErrorResponse(e, e.getMessage(), HttpStatus.BAD_REQUEST),
            HttpStatus.BAD_REQUEST
        );
    }
}
