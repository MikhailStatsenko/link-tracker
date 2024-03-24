package edu.java.scrapper.exception;

import edu.java.scrapper.dto.external.github.ErrorResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NoSuchRepositoryException extends RuntimeException {
    private final ErrorResponse response;
}
