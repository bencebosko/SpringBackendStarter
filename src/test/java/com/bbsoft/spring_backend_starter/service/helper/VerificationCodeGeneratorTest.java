package com.bbsoft.spring_backend_starter.service.helper;

import com.bbsoft.spring_backend_starter.config.providers.RandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VerificationCodeGeneratorTest {

    @Mock
    private RandomProvider randomProvider;
    @InjectMocks
    private VerificationCodeGenerator verificationCodeGenerator;

    @Test
    public void generate_ShouldGenerateMinValue() {
        // GIVEN
        var minRandomValue = 0.0;
        Mockito.when(randomProvider.random()).thenReturn(minRandomValue);
        // THEN
        Assertions.assertEquals(VerificationCodeGenerator.MIN_VALUE, verificationCodeGenerator.generate());
    }

    @Test
    public void generate_ShouldGenerateMaxValue() {
        // GIVEN
        var maxRandomValue = 1.0;
        Mockito.when(randomProvider.random()).thenReturn(maxRandomValue);
        // THEN
        Assertions.assertEquals(VerificationCodeGenerator.MAX_VALUE, verificationCodeGenerator.generate());
    }
}
