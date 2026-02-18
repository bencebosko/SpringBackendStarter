package com.bbsoft.spring_backend_starter.security;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import com.bbsoft.spring_backend_starter.exception.SpringBackendException;
import com.bbsoft.spring_backend_starter.service.dto.AuthenticatedUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class SecurityHelperTest {

    private static final Long USER_ID = 1L;
    private final SecurityHelper securityHelper = new SecurityHelper();

    @Test
    public void setAuthentication_ShouldSetAuthentication() {
        // GIVEN
        var authentication = createAuthentication();
        // WHEN
        securityHelper.setAuthentication(authentication);
        // THEN
        Assertions.assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void getAuthenticatedUser_ShouldThrowExceptionWhenAuthenticationNotFound() {
        // WHEN
        var thrownException = Assertions.assertThrows(SpringBackendException.class, securityHelper::getAuthenticatedUser);
        // THEN
        Assertions.assertEquals(ErrorCodes.SPRING_BACKEND_ERROR, thrownException.getErrorCode());
    }

    @Test
    public void getAuthenticatedUser_ShouldThrowExceptionWhenPrincipalNotFound() {
        // GIVEN
        var emptyAuthentication = new UsernamePasswordAuthenticationToken(null, null);
        securityHelper.setAuthentication(emptyAuthentication);
        // THEN
        var thrownException = Assertions.assertThrows(SpringBackendException.class, securityHelper::getAuthenticatedUser);
        Assertions.assertEquals(ErrorCodes.SPRING_BACKEND_ERROR, thrownException.getErrorCode());
    }

    @Test
    public void getAuthenticatedUser_ShouldReturnPrincipal() {
        // GIVEN
        var authentication = createAuthentication();
        // WHEN
        securityHelper.setAuthentication(authentication);
        // THEN
        Assertions.assertEquals(authentication.getPrincipal(), securityHelper.getAuthenticatedUser());
    }

    private UsernamePasswordAuthenticationToken createAuthentication() {
        var authenticatedUser = AuthenticatedUser.builder().id(USER_ID).build();
        return new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
    }
}
