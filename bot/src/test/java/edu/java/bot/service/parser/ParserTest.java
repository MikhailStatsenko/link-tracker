package edu.java.bot.service.parser;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParserTest {
    private Parser parser;

    @BeforeEach
    public void setUp() {
        parser = new TestParser();
    }

    @Test
    public void testSupportsWithMatchingHost() {
        URI uri = URI.create("http://example.com/path");
        assertThat(parser.supports(uri)).isTrue();
    }

    @Test
    public void testSupportsWithNonMatchingHost() {
        URI uri = URI.create("http://otherhost.com/path");
        assertThat(parser.supports(uri)).isFalse();
    }

    @Test
    public void testSupportsWithNullHost() {
        URI uri = mock(URI.class);
        when(uri.getHost()).thenReturn(null);

        assertThat(parser.supports(uri)).isFalse();
    }


    private static class TestParser implements Parser {
        private static final String HOST = "example.com";

        @Override
        public String host() {
            return HOST;
        }
    }
}
