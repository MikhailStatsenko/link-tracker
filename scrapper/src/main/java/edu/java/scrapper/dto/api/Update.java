package edu.java.scrapper.dto.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Update(
    @NotNull
    Long linkId,

    @NotBlank
    String url,

    @NotBlank
    String description
){
}
