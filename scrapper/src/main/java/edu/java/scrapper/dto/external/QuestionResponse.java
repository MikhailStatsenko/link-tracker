package edu.java.scrapper.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

public record QuestionResponse(List<QuestionResponseItem> items) {
    public record QuestionResponseItem(
        @NotNull
        @JsonProperty("question_id")
        Integer questionId,

        @NotNull
        @JsonProperty("last_activity_date")
        OffsetDateTime lastActivityDate
    ) {
    }
}
