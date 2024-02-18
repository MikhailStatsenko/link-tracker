package edu.java.bot.service.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GitHubHandlerTest {
    @Test
    public void testHost() {
        GitHubHandler gitHubHandler = new GitHubHandler();
        assertThat(gitHubHandler.host()).isEqualTo("github.com");
    }
}
