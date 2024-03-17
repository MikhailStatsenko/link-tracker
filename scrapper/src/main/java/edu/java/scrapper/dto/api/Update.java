package edu.java.scrapper.dto.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record Update(
    @NotNull
    Long linkId,

    @NotBlank
    String url,

    @NotBlank
    String description,

    @NotNull
    OffsetDateTime updateTime
){
}
