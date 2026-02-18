package com.bbsoft.spring_backend_starter.controller.helper;

import com.bbsoft.spring_backend_starter.config.properties.RateLimiterPeriod;
import com.bbsoft.spring_backend_starter.exception.RateLimiterException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class KeyBasedRateLimiter<K> {

    private final RateLimiterPeriod rateLimiterPeriod;
    private final Cache<K, Bucket> keysToBucket;

    KeyBasedRateLimiter(RateLimiterPeriod rateLimiterPeriod, int cacheSize, int cacheExpirationSeconds) {
        this.rateLimiterPeriod = rateLimiterPeriod;
        keysToBucket = Caffeine.newBuilder().maximumSize(cacheSize).expireAfterAccess(cacheExpirationSeconds, TimeUnit.SECONDS).build();
    }

    public void acquireOrThrow(K key, Supplier<RateLimiterException> exceptionSupplier) {
        var bucket = keysToBucket.get(key, key_ -> createBucket());
        if (Objects.nonNull(bucket) && !bucket.tryConsume(1)) {
            throw exceptionSupplier.get();
        }
    }

    private Bucket createBucket() {
        final var requestCount = rateLimiterPeriod.requestCount();
        var timeIntervalSeconds = rateLimiterPeriod.timeIntervalSeconds();
        var bandwidth = Bandwidth.builder()
            .capacity(requestCount)
            .refillGreedy(requestCount, Duration.ofSeconds(timeIntervalSeconds))
            .build();
        return Bucket.builder().addLimit(bandwidth).build();
    }
}
