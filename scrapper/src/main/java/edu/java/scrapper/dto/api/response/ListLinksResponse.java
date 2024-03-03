package edu.java.scrapper.dto.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ListLinksResponse(
    @NotEmpty
    @JsonProperty("links")
    List<LinkResponse> links,

    @NotNull
    @JsonProperty("size")
    Integer size
) {
}

