package com.bbsoft.spring_backend_starter.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.HandlerExceptionResolver;

@ExtendWith(MockitoExtension.class)
public class AccessDeniedExceptionResolverTest {

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;
    @InjectMocks
    private AccessDeniedExceptionResolver accessDeniedExceptionResolver;

    @Test
    public void commence_ShouldCallExceptionResolver() {
        // GIVEN
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var accessDeniedException = Mockito.mock(AccessDeniedException.class);
        // WHEN
        accessDeniedExceptionResolver.handle(request, response, accessDeniedException);
        // THEN
        Mockito.verify(handlerExceptionResolver, Mockito.times(1)).resolveException(request, response, null, accessDeniedException);
    }
}
