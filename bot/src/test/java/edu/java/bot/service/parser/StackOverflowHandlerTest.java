package edu.java.bot.service.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class StackOverflowHandlerTest {

    @Test
    public void testHost() {
        StackOverflowHandler stackOverflowHandler = new StackOverflowHandler();
        assertThat(stackOverflowHandler.host()).isEqualTo("stackoverflow.com");
    }
}
