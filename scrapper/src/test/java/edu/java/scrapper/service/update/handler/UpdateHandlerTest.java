package edu.java.scrapper.service.update.handler;

import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateHandlerTest {
    private UpdateHandler updateHandler;

    @BeforeEach
    public void setUp() {
        updateHandler = new TestUpdateHandler();
    }

    @Test
    public void testSupportsWithMatchingHost() {
        URI uri = URI.create("http://example.com/path");
        assertThat(updateHandler.supports(uri)).isTrue();
    }

    @Test
    public void testSupportsWithNonMatchingHost() {
        URI uri = URI.create("http://otherhost.com/path");
        assertThat(updateHandler.supports(uri)).isFalse();
    }

    @Test
    public void testSupportsWithNullHost() {
        URI uri = mock(URI.class);
        when(uri.getHost()).thenReturn(null);

        assertThat(updateHandler.supports(uri)).isFalse();
    }

    private static class TestUpdateHandler implements UpdateHandler {
        private static final String HOST = "example.com";

        @Override
        public String host() {
            return HOST;
        }

        @Override
        public List<Optional<Update>> fetchUpdates(Link link) {
            return List.of();
        }
    }
}
