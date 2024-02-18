package edu.java.bot.service.parser;

import org.springframework.stereotype.Service;

@Service
public class GitHubHandler implements Handler {
    private static final String HOST = "github.com";

    @Override
    public String host() {
        return HOST;
    }
}
