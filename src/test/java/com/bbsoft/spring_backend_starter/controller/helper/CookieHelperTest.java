package com.bbsoft.spring_backend_starter.controller.helper;

import com.bbsoft.spring_backend_starter.config.properties.TokenProperties;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class CookieHelperTest {

    private static final String TOKEN = "token";

    @Mock
    private TokenProperties tokenProperties;
    @InjectMocks
    private CookieHelper cookieHelper;

    @Test
    public void createAccessCookie_ShouldCreateCookieWithExpectedAge() {
        // GIVEN
        var validityMinutes = 1;
        var expectedAgeSeconds = validityMinutes * 60;
        var expectedAccessCookie = "access-token=token; Path=/; Max-Age=" + expectedAgeSeconds + "; Secure; HttpOnly; SameSite=Strict";
        Mockito.when(tokenProperties.getAccessTokenValidityMinutes()).thenReturn(validityMinutes);
        // WHEN
        var accessCookie = cookieHelper.createAccessCookie(TOKEN);
        // THEN
        Assertions.assertEquals(expectedAccessCookie, removeExpirationPart(accessCookie));
    }

    @Test
    public void createRefreshCookie_ShouldCreateCookieWithExpectedAge() {
        // GIVEN
        var expirationDays = 1;
        var expectedAgeSeconds = expirationDays * 24 * 60 * 60;
        var expectedRefreshCookie = "refresh-token=token; Path=/api/auth/refresh; Max-Age=" + expectedAgeSeconds + "; Secure; HttpOnly; SameSite=Strict";
        Mockito.when(tokenProperties.getRefreshTokenExpirationDays()).thenReturn(expirationDays);
        // WHEN
        var refreshCookie = cookieHelper.createRefreshCookie(TOKEN);
        // THEN
        Assertions.assertEquals(expectedRefreshCookie, removeExpirationPart(refreshCookie));
    }

    @Test
    public void createExpiredAccessCookie_ShouldCreateTokenCookieWithZeroAge() {
        // GIVEN
        var expectedAccessCookie = "access-token=; Path=/; Max-Age=0; Secure; HttpOnly; SameSite=Strict";
        // WHEN
        var accessCookie = cookieHelper.createExpiredAccessCookie();
        // THEN
        Assertions.assertEquals(expectedAccessCookie, removeExpirationPart(accessCookie));
    }

    @Test
    public void createExpiredRefreshCookie_ShouldCreateCookieWithZeroAge() {
        // GIVEN
        var expectedRefreshCookie = "refresh-token=; Path=/api/auth/refresh; Max-Age=0; Secure; HttpOnly; SameSite=Strict";
        // WHEN
        var refreshCookie = cookieHelper.createExpiredRefreshCookie();
        // THEN
        Assertions.assertEquals(expectedRefreshCookie, removeExpirationPart(refreshCookie));
    }

    @Test
    public void getAccessCookie_ShouldReturnNullIfAccessCookieNotFound() {
        // GIVEN
        var mockHttpRequest = new MockHttpServletRequest();
        // WHEN
        Assertions.assertNull(cookieHelper.getAccessCookie(mockHttpRequest));
    }

    @Test
    public void getAccessCookie_ShouldReturnTheAccessCookie() {
        // GIVEN
        var mockHttpRequest = new MockHttpServletRequest();
        var accessCookie = new Cookie(CookieHelper.ACCESS_TOKEN, TOKEN);
        mockHttpRequest.setCookies(accessCookie);
        // WHEN
        Assertions.assertEquals(accessCookie, cookieHelper.getAccessCookie(mockHttpRequest));
    }

    @Test
    public void getRefreshCookie_ShouldReturnNullIfRefreshCookieNotFound() {
        // GIVEN
        var mockHttpRequest = new MockHttpServletRequest();
        // WHEN
        Assertions.assertNull(cookieHelper.getRefreshCookie(mockHttpRequest));
    }

    @Test
    public void getRefreshCookie_ShouldReturnTheRefreshCookie() {
        // GIVEN
        var mockHttpRequest = new MockHttpServletRequest();
        var refreshCookie = new Cookie(CookieHelper.REFRESH_TOKEN, TOKEN);
        mockHttpRequest.setCookies(refreshCookie);
        // WHEN
        Assertions.assertEquals(refreshCookie, cookieHelper.getRefreshCookie(mockHttpRequest));
    }

    private String removeExpirationPart(String cookie) {
        return cookie.replaceFirst(" Expires=[^;]*;", "");
    }
}
