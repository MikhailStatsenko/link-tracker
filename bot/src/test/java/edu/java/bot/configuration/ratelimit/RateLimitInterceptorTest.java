package edu.java.bot.configuration.ratelimit;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.assertj.core.api.Assertions.assertThat;

class RateLimitInterceptorTest {
    @InjectMocks
    private RateLimitInterceptor interceptor;

    @BeforeEach
    public void setUp() {
        interceptor = new RateLimitInterceptor(10, 60);
    }

    @Test
    public void testPreHandleSuccessfulRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = interceptor.preHandle(request, response, null);

        assertThat(result).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void testPreHandleTooManyRequests() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        for (int i = 0; i < 10; i++) {
            interceptor.preHandle(request, response, null);
        }
        boolean result = interceptor.preHandle(request, response, null);

        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getErrorMessage()).isEqualTo("You have exhausted your API Request Quota");
    }

    @Test
    public void testResolveBucketCacheHit() {
        Bucket createdBucket = interceptor.resolveBucket("127.0.0.1");

        Bucket resolvedBucket = interceptor.resolveBucket("127.0.0.1");

        assertThat(resolvedBucket).isSameAs(createdBucket);
    }
}
