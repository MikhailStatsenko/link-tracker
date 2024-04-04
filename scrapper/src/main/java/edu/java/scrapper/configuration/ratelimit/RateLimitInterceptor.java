package edu.java.scrapper.configuration.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

public class RateLimitInterceptor implements HandlerInterceptor {
    private final int tokenCount;
    private final int refillPeriod;
    private final Cache<String, Bucket> cache;

    public RateLimitInterceptor(int tokenCount, int refillPeriod) {
        this.tokenCount = tokenCount;
        this.refillPeriod = refillPeriod;
        this.cache = Caffeine.newBuilder()
            .expireAfterAccess(refillPeriod, TimeUnit.SECONDS)
            .build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        Bucket tokenBucket = resolveBucket(request.getRemoteAddr());
        boolean isConsumed = tokenBucket.tryConsume(1);
        if (isConsumed) {
            return true;
        } else {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have exhausted your API Request Quota");
            return false;
        }
    }

    public Bucket resolveBucket(String ipAddress) {
        return cache.get(ipAddress, this::createBucket);
    }

    private Bucket createBucket(String ipAddress) {
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(tokenCount)
                .refillIntervally(tokenCount, Duration.ofSeconds(refillPeriod)))
            .build();
    }
}
