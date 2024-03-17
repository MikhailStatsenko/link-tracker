package edu.java.scrapper.model;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Link {
    private Long id;

    @NotNull
    private URI url;

    @NotNull
    private OffsetDateTime lastCheckTime;

    public Link(URI url) {
        this.url = url;
        lastCheckTime = OffsetDateTime.now();
    }
}
