package com.bbsoft.spring_backend_starter.controller.helper;

import com.bbsoft.spring_backend_starter.config.properties.RateLimiterProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RateLimiterFactoryTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RateLimiterProperties rateLimiterProperties;
    @InjectMocks
    private RateLimiterFactory rateLimiterFactory;

    @Test
    public void getRequestPerUsernameLimiter_ShouldCreateOnlyOneInstancePerName() {
        // GIVEN
        var rateLimiterName = "rateLimiter";
        // WHEN
        var first = rateLimiterFactory.getRequestPerUsernameLimiter(rateLimiterName);
        var second = rateLimiterFactory.getRequestPerUsernameLimiter(rateLimiterName);
        // THEN
        Mockito.verify(rateLimiterProperties, Mockito.times(1)).getCacheSize();
        Mockito.verify(rateLimiterProperties, Mockito.times(1)).getCacheExpirationSeconds();
        Mockito.verify(rateLimiterProperties, Mockito.times(1)).getRequestsPerUsername();
        Assertions.assertSame(first, second);
    }

    @Test
    public void getRequestPerTokenLimiter_ShouldCreateOnlyOneInstancePerName() {
        // GIVEN
        var rateLimiterName = "rateLimiter";
        // WHEN
        var first = rateLimiterFactory.getRequestPerTokenLimiter(rateLimiterName);
        var second = rateLimiterFactory.getRequestPerTokenLimiter(rateLimiterName);
        // THEN
        Mockito.verify(rateLimiterProperties, Mockito.times(1)).getCacheSize();
        Mockito.verify(rateLimiterProperties, Mockito.times(1)).getCacheExpirationSeconds();
        Mockito.verify(rateLimiterProperties, Mockito.times(1)).getRequestsPerToken();
        Assertions.assertSame(first, second);
    }
}
