package com.bbsoft.spring_backend_starter.controller.helper;

import com.bbsoft.spring_backend_starter.config.properties.RateLimiterPeriod;
import com.bbsoft.spring_backend_starter.exception.RateLimiterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.awaitility.Awaitility.await;

@ExtendWith(MockitoExtension.class)
public class KeyBasedRateLimiterTest {

    private static final String KEY = "key";
    private static final RateLimiterException EXCEPTION = new RateLimiterException("Rate limit exceeded.");
    private static final int CACHE_SIZE = 1;
    private static final int CACHE_EXPIRATION_SECONDS = Integer.MAX_VALUE;

    @Mock
    private RateLimiterPeriod rateLimiterPeriod;
    private KeyBasedRateLimiter<String> keyBasedRateLimiter;

    @BeforeEach
    public void initMocks() {
        keyBasedRateLimiter = new KeyBasedRateLimiter<>(rateLimiterPeriod, CACHE_SIZE, CACHE_EXPIRATION_SECONDS);
    }

    @Test
    public void acquireOrThrow_ShouldNotThrowExceptionIfBucketCapacityNotExceeded() {
        // GIVEN
        var requestCount = 1;
        var timeIntervalSeconds = 999L;
        Mockito.when(rateLimiterPeriod.requestCount()).thenReturn(requestCount);
        Mockito.when(rateLimiterPeriod.timeIntervalSeconds()).thenReturn(timeIntervalSeconds);
        // THEN
        Assertions.assertDoesNotThrow(() -> keyBasedRateLimiter.acquireOrThrow(KEY, () -> EXCEPTION));
    }

    @Test
    public void acquireOrThrow_ShouldThrowExceptionIfBucketCapacityExceeded() {
        // GIVEN
        var requestCount = 1;
        var timeIntervalSeconds = 999L;
        Mockito.when(rateLimiterPeriod.requestCount()).thenReturn(requestCount);
        Mockito.when(rateLimiterPeriod.timeIntervalSeconds()).thenReturn(timeIntervalSeconds);
        // THEN
        Assertions.assertDoesNotThrow(() -> keyBasedRateLimiter.acquireOrThrow(KEY, () -> EXCEPTION));
        Assertions.assertThrows(RateLimiterException.class, () -> keyBasedRateLimiter.acquireOrThrow(KEY, () -> EXCEPTION));
    }

    @Test
    public void acquireOrThrow_ShouldRefreshBucketCapacityAfterPeriodEnds() {
        // GIVEN
        var requestCount = 1;
        var timeIntervalSeconds = 1L;
        Mockito.when(rateLimiterPeriod.requestCount()).thenReturn(requestCount);
        Mockito.when(rateLimiterPeriod.timeIntervalSeconds()).thenReturn(timeIntervalSeconds);
        keyBasedRateLimiter.acquireOrThrow(KEY, () -> EXCEPTION);
        // THEN
        Assertions.assertThrows(RateLimiterException.class, () -> keyBasedRateLimiter.acquireOrThrow(KEY, () -> EXCEPTION));
        await().atMost(Duration.ofSeconds(timeIntervalSeconds + 1))
            .untilAsserted(() -> Assertions.assertDoesNotThrow(() -> keyBasedRateLimiter.acquireOrThrow(KEY, () -> EXCEPTION)));
    }
}
