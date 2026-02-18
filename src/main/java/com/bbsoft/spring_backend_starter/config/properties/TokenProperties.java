package com.bbsoft.spring_backend_starter.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "spring-backend.token")
@Getter
@RequiredArgsConstructor
@Validated
public class TokenProperties {

    @NotNull
    private final String encryptionKey;
    @NotNull
    private final Integer refreshTokenExpirationDays;
    @NotNull
    private final Integer accessTokenValidityMinutes;
}
