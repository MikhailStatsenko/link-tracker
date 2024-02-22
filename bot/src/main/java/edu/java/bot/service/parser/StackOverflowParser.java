package edu.java.bot.service.parser;

import org.springframework.stereotype.Service;

@Service
public class StackOverflowParser implements Parser {
    private static final String HOST = "stackoverflow.com";

    @Override
    public String host() {
        return HOST;
    }
}
