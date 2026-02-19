package com.bbsoft.spring_backend_starter.controller;

import com.bbsoft.spring_backend_starter.controller.helper.CookieHelper;
import com.bbsoft.spring_backend_starter.controller.helper.RateLimiterFactory;
import com.bbsoft.spring_backend_starter.exception.AuthException;
import com.bbsoft.spring_backend_starter.exception.RateLimiterException;
import com.bbsoft.spring_backend_starter.security.JwtTokenHelper;
import com.bbsoft.spring_backend_starter.security.SecurityHelper;
import com.bbsoft.spring_backend_starter.service.AuthService;
import com.bbsoft.spring_backend_starter.service.dto.AuthRequest;
import com.bbsoft.spring_backend_starter.service.dto.AuthResponse;
import com.bbsoft.spring_backend_starter.service.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private static final String AUTHENTICATE_LIMITER = "authenticateLimiter";
    private static final String LOGIN_LIMITER = "loginLimiter";
    private static final String REFRESH_LIMITER = "refreshLimiter";

    private final AuthService authService;
    private final SecurityHelper securityHelper;
    private final CookieHelper cookieHelper;
    private final JwtTokenHelper jwtTokenHelper;
    private final RateLimiterFactory rateLimiterFactory;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@Validated @RequestBody AuthRequest authRequest) {
        rateLimiterFactory.getRequestPerUsernameLimiter(AUTHENTICATE_LIMITER).acquireOrThrow(authRequest.getUsernameOrEmail(), () -> new RateLimiterException("Authenticate request limit exceeded."));
        log.info("Authenticating user with username or email: {}", authRequest.getUsernameOrEmail());
        return ResponseEntity.ok().body(authService.authenticate(authRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Validated @RequestBody LoginRequest loginRequest) {
        rateLimiterFactory.getRequestPerUsernameLimiter(LOGIN_LIMITER).acquireOrThrow(loginRequest.getUsernameOrEmail(), () -> new RateLimiterException("Login request limit exceeded."));
        log.info("Logging in user with username or email: {}", loginRequest.getUsernameOrEmail());
        var tokenDTO = authService.login(loginRequest);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookieHelper.createAccessCookie(tokenDTO.getAccessToken()))
            .header(HttpHeaders.SET_COOKIE, cookieHelper.createRefreshCookie(tokenDTO.getRefreshToken()))
            .body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        var authenticatedUser = securityHelper.getAuthenticatedUser();
        log.info("Logging out user: {}", authenticatedUser.getId());
        authService.logout(authenticatedUser.getId());
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookieHelper.createExpiredAccessCookie())
            .header(HttpHeaders.SET_COOKIE, cookieHelper.createExpiredRefreshCookie())
            .body(null);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request) {
        final var refreshCookie = cookieHelper.getRefreshCookie(request);
        if (Objects.isNull(refreshCookie)) {
            throw AuthException.createAuthenticationRequired("Refresh token is missing.");
        }
        final var refreshToken = refreshCookie.getValue();
        var authenticatedUser = jwtTokenHelper.parseRefreshToken(refreshToken);
        rateLimiterFactory.getRequestPerTokenLimiter(REFRESH_LIMITER).acquireOrThrow(refreshToken, () -> new RateLimiterException("Refresh request limit exceeded."));
        log.info("Refreshing access token for user: {}", authenticatedUser.getId());
        var accessToken = authService.refreshAccessToken(authenticatedUser, refreshToken);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookieHelper.createAccessCookie(accessToken))
            .body(null);
    }
}
