package com.bbsoft.spring_backend_starter.security;

import com.bbsoft.spring_backend_starter.controller.helper.CookieHelper;
import com.bbsoft.spring_backend_starter.controller.helper.KeyBasedRateLimiter;
import com.bbsoft.spring_backend_starter.controller.helper.RateLimiterFactory;
import com.bbsoft.spring_backend_starter.exception.RateLimiterException;
import com.bbsoft.spring_backend_starter.service.dto.AuthenticatedUser;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    private static final String TOKEN = "token";
    private static final long USER_ID = 1L;
    private static final Cookie ACCESS_COOKIE = new Cookie(CookieHelper.ACCESS_TOKEN, TOKEN);
    private static final HttpServletResponse HTTP_RESPONSE = new MockHttpServletResponse();
    private static final Exception JWT_EXCEPTION = new JwtException("Token is invalid");

    @Mock
    private JwtTokenHelper jwtTokenHelper;
    @Mock
    private HandlerExceptionResolver exceptionResolver;
    @Spy
    private SecurityHelper securityHelper = new SecurityHelper();
    @Mock
    private CookieHelper cookieHelper;
    @Mock
    private FilterChain filterChain;
    @Mock
    private RateLimiterFactory rateLimiterFactory;
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private KeyBasedRateLimiter<String> authenticatedRequestLimiter;

    @AfterEach
    public void cleanUp() {
        securityHelper.setAuthentication(null);
    }

    @Test
    public void doFilterInternal_ShouldNotSetAuthenticationIfAccessCookieIsMissing() throws ServletException, IOException {
        // GIVEN
        var httpRequest = new MockHttpServletRequest();
        Mockito.when(cookieHelper.getAccessCookie(httpRequest)).thenReturn(null);
        // WHEN
        jwtAuthenticationFilter.doFilterInternal(httpRequest, HTTP_RESPONSE, filterChain);
        // THEN
        Mockito.verify(jwtTokenHelper, never()).parseAccessToken(any());
        Mockito.verify(securityHelper, never()).setAuthentication(any());
        Mockito.verify(filterChain, times(1)).doFilter(httpRequest, HTTP_RESPONSE);
    }

    @Test
    public void doFilterInternal_ShouldResolveExceptionIfAccessTokenIsInvalid() throws ServletException, IOException {
        // GIVEN
        var httpRequest = new MockHttpServletRequest();
        Mockito.when(cookieHelper.getAccessCookie(httpRequest)).thenReturn(ACCESS_COOKIE);
        Mockito.when(jwtTokenHelper.parseAccessToken(TOKEN)).thenThrow(JWT_EXCEPTION);
        // WHEN
        jwtAuthenticationFilter.doFilterInternal(httpRequest, HTTP_RESPONSE, filterChain);
        // THEN
        Mockito.verify(jwtTokenHelper, times(1)).parseAccessToken(TOKEN);
        Mockito.verify(authenticatedRequestLimiter, never()).acquireOrThrow(eq(TOKEN), any());
        Mockito.verify(exceptionResolver, times(1)).resolveException(httpRequest, HTTP_RESPONSE, null, JWT_EXCEPTION);
        Mockito.verify(filterChain, never()).doFilter(httpRequest, HTTP_RESPONSE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void doFilterInternal_ShouldSetAuthenticationIfAccessTokenIsValid() throws ServletException, IOException {
        // GIVEN
        var httpRequest = new MockHttpServletRequest();
        var authenticatedUser = createAuthenticatedUser();
        var expectedAuthentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
        Mockito.when(cookieHelper.getAccessCookie(httpRequest)).thenReturn(ACCESS_COOKIE);
        Mockito.when(jwtTokenHelper.parseAccessToken(TOKEN)).thenReturn(authenticatedUser);
        Mockito.when(rateLimiterFactory.getRequestPerTokenLimiter(JwtAuthenticationFilter.AUTHENTICATED_REQUEST_LIMITER)).thenReturn(authenticatedRequestLimiter);
        // WHEN
        jwtAuthenticationFilter.doFilterInternal(httpRequest, HTTP_RESPONSE, filterChain);
        // THEN
        var supplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.verify(jwtTokenHelper, times(1)).parseAccessToken(TOKEN);
        Mockito.verify(authenticatedRequestLimiter, times(1)).acquireOrThrow(eq(TOKEN), supplierCaptor.capture());
        Mockito.verify(securityHelper, times(1)).setAuthentication(expectedAuthentication);
        Mockito.verify(filterChain, times(1)).doFilter(httpRequest, HTTP_RESPONSE);
        Assertions.assertEquals(RateLimiterException.class, supplierCaptor.getValue().get().getClass());
    }

    private AuthenticatedUser createAuthenticatedUser() {
        return AuthenticatedUser.builder().id(USER_ID).build();
    }
}
