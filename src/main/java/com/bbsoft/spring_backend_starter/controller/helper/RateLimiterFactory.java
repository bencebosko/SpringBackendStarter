package com.bbsoft.spring_backend_starter.controller.helper;

import com.bbsoft.spring_backend_starter.config.properties.RateLimiterPeriod;
import com.bbsoft.spring_backend_starter.config.properties.RateLimiterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class RateLimiterFactory {

    private final RateLimiterProperties rateLimiterProperties;
    private final Map<String, KeyBasedRateLimiter> rateLimitersByName = new ConcurrentHashMap<>();

    public KeyBasedRateLimiter<String> getRequestPerUsernameLimiter(String rateLimiterName) {
        return createRateLimiter(rateLimiterName, rateLimiterProperties::getRequestsPerUsername);
    }

    public KeyBasedRateLimiter<String> getRequestPerTokenLimiter(String rateLimiterName) {
        return createRateLimiter(rateLimiterName, rateLimiterProperties::getRequestsPerToken);
    }

    private KeyBasedRateLimiter createRateLimiter(String name, Supplier<RateLimiterPeriod> periodSupplier) {
        return rateLimitersByName.computeIfAbsent(name, key -> new KeyBasedRateLimiter<>(periodSupplier.get(), rateLimiterProperties.getCacheSize(), rateLimiterProperties.getCacheExpirationSeconds()));
    }
}
