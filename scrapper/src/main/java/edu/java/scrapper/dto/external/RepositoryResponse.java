package edu.java.scrapper.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record RepositoryResponse(
    @NotNull
    @JsonProperty("id")
    Long id,

    @NotNull
    @JsonProperty("name")
    String name,

    @NotNull
    @JsonProperty("full_name")
    String fullName,

    @NotNull
    @JsonProperty("updated_at")
    OffsetDateTime updatedAt,

    @NotNull
    @JsonProperty("pushed_at")
    OffsetDateTime pushedAt
) {
}
