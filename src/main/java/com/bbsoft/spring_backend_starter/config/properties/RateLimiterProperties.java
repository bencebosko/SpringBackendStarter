package com.bbsoft.spring_backend_starter.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "spring-backend.rate-limiter")
@Getter
@RequiredArgsConstructor
@Validated
public class RateLimiterProperties {

    @Valid
    @NestedConfigurationProperty
    private final RateLimiterPeriod requestsPerUsername;
    @Valid
    @NestedConfigurationProperty
    private final RateLimiterPeriod requestsPerToken;
    @NotNull
    private final Integer cacheSize;
    @NotNull
    private final Integer cacheExpirationSeconds;
}
