package edu.java.bot.service.parser;

import org.springframework.stereotype.Service;

@Service
public class StackOverflowHandler implements Handler {
    private static final String HOST = "stackoverflow.com";

    @Override
    public String host() {
        return HOST;
    }
}
