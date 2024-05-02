package edu.java.bot.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(
    @NotNull
    @JsonProperty("id")
    Long id,

    @NotNull
    @JsonProperty("url")
    URI url,

    @JsonProperty("description")
    String description,

    @NotEmpty
    @JsonProperty("tgChatIds")
    List<Long> tgChatIds
){
}

