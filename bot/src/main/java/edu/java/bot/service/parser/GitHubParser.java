package edu.java.bot.service.parser;

import org.springframework.stereotype.Service;

@Service
public class GitHubParser implements Parser {
    private static final String HOST = "github.com";

    @Override
    public String host() {
        return HOST;
    }
}
