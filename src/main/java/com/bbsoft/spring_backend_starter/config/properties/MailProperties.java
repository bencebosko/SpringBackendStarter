package com.bbsoft.spring_backend_starter.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "spring-backend.mail")
@Getter
@RequiredArgsConstructor
@Validated
public class MailProperties {

    @NotNull
    private final String fromAddress;
}
