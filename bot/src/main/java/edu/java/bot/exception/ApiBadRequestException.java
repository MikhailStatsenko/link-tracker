package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;


public class ApiBadRequestException extends WebClientResponseException {
    private final ApiErrorResponse apiErrorResponse;

    public ApiBadRequestException(ApiErrorResponse apiErrorResponse) {
        super(HttpStatus.BAD_REQUEST.value(), "Bad request", HttpHeaders.EMPTY, null, Charset.defaultCharset());
        this.apiErrorResponse = apiErrorResponse;
    }
}
