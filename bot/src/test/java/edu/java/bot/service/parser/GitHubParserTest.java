package edu.java.bot.service.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GitHubParserTest {
    @Test
    public void testHost() {
        GitHubParser gitHubParser = new GitHubParser();
        assertThat(gitHubParser.host()).isEqualTo("github.com");
    }
}
