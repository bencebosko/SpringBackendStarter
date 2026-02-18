package com.bbsoft.spring_backend_starter.security;

import com.bbsoft.spring_backend_starter.controller.helper.CookieHelper;
import com.bbsoft.spring_backend_starter.controller.helper.RateLimiterFactory;
import com.bbsoft.spring_backend_starter.exception.RateLimiterException;
import com.bbsoft.spring_backend_starter.service.dto.AuthenticatedUser;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHENTICATED_REQUEST_LIMITER = "authenticatedRequestLimiter";

    private final CookieHelper cookieHelper;
    private final JwtTokenHelper jwtTokenHelper;
    private final HandlerExceptionResolver exceptionResolver;
    private final SecurityHelper securityHelper;
    private final RateLimiterFactory rateLimiterFactory;

    @Autowired
    public JwtAuthenticationFilter(CookieHelper cookieHelper,
                                   JwtTokenHelper jwtTokenHelper,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver,
                                   SecurityHelper securityHelper,
                                   RateLimiterFactory rateLimiterFactory) {
        this.jwtTokenHelper = jwtTokenHelper;
        this.exceptionResolver = exceptionResolver;
        this.securityHelper = securityHelper;
        this.cookieHelper = cookieHelper;
        this.rateLimiterFactory = rateLimiterFactory;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        AuthenticatedUser authenticatedUser = null;
        var accessCookie = cookieHelper.getAccessCookie(request);
        if (Objects.nonNull(accessCookie)) {
            final var accessToken = accessCookie.getValue();
            try {
                authenticatedUser = jwtTokenHelper.parseAccessToken(accessToken);
                rateLimiterFactory.getRequestPerTokenLimiter(AUTHENTICATED_REQUEST_LIMITER).acquireOrThrow(accessToken, () -> new RateLimiterException("Authenticated request limit exceeded."));
            } catch (JwtException | RateLimiterException ex) {
                exceptionResolver.resolveException(request, response, null, ex);
                return;
            }
        }
        if (Objects.nonNull(authenticatedUser)) {
            var authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
            securityHelper.setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
