package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiNotFoundException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;
}
