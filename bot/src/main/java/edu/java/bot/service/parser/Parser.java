package edu.java.bot.service.parser;

import java.net.URI;

public interface Parser {
    String host();

    default boolean supports(URI uri) {
        return uri.getHost() != null && uri.getHost().equals(host());
    }
}
