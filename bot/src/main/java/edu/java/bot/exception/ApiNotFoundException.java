package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class ApiNotFoundException extends WebClientResponseException {
    private final ApiErrorResponse apiErrorResponse;

    public ApiNotFoundException(ApiErrorResponse apiErrorResponse) {
        super(HttpStatus.NOT_FOUND.value(), "Bad request", HttpHeaders.EMPTY, null, Charset.defaultCharset());
        this.apiErrorResponse = apiErrorResponse;
    }
}
