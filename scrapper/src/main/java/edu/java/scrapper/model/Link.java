package edu.java.scrapper.model;

import java.net.URI;
import lombok.Data;

@Data
public class Link {
    private Long id;

    private URI url;

    public Link(URI url) {
        this.url = url;
    }
}
