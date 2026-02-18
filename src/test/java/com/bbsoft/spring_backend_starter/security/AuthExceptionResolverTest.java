package com.bbsoft.spring_backend_starter.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.HandlerExceptionResolver;

@ExtendWith(MockitoExtension.class)
public class AuthExceptionResolverTest {

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;
    @InjectMocks
    private AuthExceptionResolver authExceptionResolver;

    @Test
    public void commence_ShouldCallExceptionResolver() {
        // GIVEN
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var authenticationException = Mockito.mock(AuthenticationException.class);
        // WHEN
        authExceptionResolver.commence(request, response, authenticationException);
        // THEN
        Mockito.verify(handlerExceptionResolver, Mockito.times(1)).resolveException(request, response, null, authenticationException);
    }
}
