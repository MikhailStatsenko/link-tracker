package edu.java.bot.service.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class StackOverflowParserTest {

    @Test
    public void testHost() {
        StackOverflowParser stackOverflowParser = new StackOverflowParser();
        assertThat(stackOverflowParser.host()).isEqualTo("stackoverflow.com");
    }
}
