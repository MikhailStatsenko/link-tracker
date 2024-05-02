package edu.java.scrapper.exception;

import edu.java.scrapper.dto.external.github.ErrorResponse;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class NoSuchRepositoryException extends WebClientResponseException {
    private final ErrorResponse response;

    public NoSuchRepositoryException(ErrorResponse response) {
        super(HttpStatus.NOT_FOUND.value(), "Repository Not Found", HttpHeaders.EMPTY, null, Charset.defaultCharset());
        this.response = response;
    }
}
