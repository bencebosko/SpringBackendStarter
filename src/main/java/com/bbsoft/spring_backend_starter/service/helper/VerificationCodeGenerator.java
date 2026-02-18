package com.bbsoft.spring_backend_starter.service.helper;

import com.bbsoft.spring_backend_starter.config.providers.RandomProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationCodeGenerator {

    public static final int MIN_VALUE = 100000;
    public static final int MAX_VALUE = 999999;

    private final RandomProvider randomProvider;

    public int generate() {
        return (int) (MIN_VALUE + randomProvider.random() * (MAX_VALUE - MIN_VALUE));
    }
}
