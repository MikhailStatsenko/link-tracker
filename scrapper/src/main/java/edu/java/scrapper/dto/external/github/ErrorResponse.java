package edu.java.scrapper.dto.external.github;

import jakarta.validation.constraints.NotBlank;

public record ErrorResponse(
    @NotBlank
    String message
) {
}
