package com.bbsoft.spring_backend_starter.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "spring-backend")
@Getter
@RequiredArgsConstructor
@Validated
public class SpringBackendProperties {

    @NotNull
    private final String defaultUser;
    @NotNull
    private final String defaultPassword;
    @NotNull
    private final String defaultEmail;
    @NotNull
    private final Boolean loginVerificationEnabled;
    private final TokenProperties tokenProperties;
    private final MailProperties mailProperties;
    private final RateLimiterProperties rateLimiterProperties;
}
