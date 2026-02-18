package com.bbsoft.spring_backend_starter.controller.helper;

import com.bbsoft.spring_backend_starter.config.properties.TokenProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CookieHelper {

    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";

    private static final boolean HTTP_ONLY = true;
    private static final boolean SECURE = true;
    private static final String SAME_SITE = "Strict";
    private static final String PATH_ALL = "/";
    private static final String PATH_REFRESH = "/api/auth/refresh";

    private final TokenProperties tokenProperties;

    public String createAccessCookie(String accessToken) {
        return createAccessCookie(accessToken, true);
    }

    public String createExpiredAccessCookie() {
        return createAccessCookie(null, false);
    }

    public String createRefreshCookie(String refreshToken) {
        return createRefreshCookie(refreshToken, true);
    }

    public String createExpiredRefreshCookie() {
        return createRefreshCookie(null, false);
    }

    public Cookie getAccessCookie(HttpServletRequest request) {
        if (Objects.isNull(request.getCookies())) {
            return null;
        }
        return Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(ACCESS_TOKEN)).findFirst().orElse(null);
    }

    public Cookie getRefreshCookie(HttpServletRequest request) {
        if (Objects.isNull(request.getCookies())) {
            return null;
        }
        return Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(REFRESH_TOKEN)).findFirst().orElse(null);
    }

    private String createAccessCookie(@Nullable String accessToken, boolean isValid) {
        return ResponseCookie.from(ACCESS_TOKEN, accessToken)
            .httpOnly(HTTP_ONLY)
            .secure(SECURE)
            .sameSite(SAME_SITE)
            .maxAge(isValid ? tokenProperties.getAccessTokenValidityMinutes() * 60 : 0)
            .path(PATH_ALL)
            .build().toString();
    }

    private String createRefreshCookie(@Nullable String refreshToken, boolean isValid) {
        return ResponseCookie.from(REFRESH_TOKEN, refreshToken)
            .httpOnly(HTTP_ONLY)
            .secure(SECURE)
            .sameSite(SAME_SITE)
            .maxAge(isValid ? tokenProperties.getRefreshTokenExpirationDays() * 24 * 60 * 60 : 0)
            .path(PATH_REFRESH)
            .build().toString();
    }
}
