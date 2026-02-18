package com.bbsoft.spring_backend_starter.config.properties;

import jakarta.validation.constraints.NotNull;

public record RateLimiterPeriod(@NotNull Integer requestCount, @NotNull Long timeIntervalSeconds) {
}
