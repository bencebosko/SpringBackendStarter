package com.bbsoft.spring_backend_starter.security;

import com.bbsoft.spring_backend_starter.config.providers.ClockProvider;
import com.bbsoft.spring_backend_starter.config.properties.TokenProperties;
import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.service.mapper.RoleMapper;
import com.bbsoft.spring_backend_starter.service.mapper.RoleMapperImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class JwtTokenHelperTest {

    private static final String ENCRYPTION_KEY = "404E635266556A586E3272EW357ADJ53AEG87GZ82F413F4428D4Z7DSY2B4B6250645367566B5970E354G3245234GZ49826EAD234BCS";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTYiLCJuYW1lIjoiVGVzdCBVc2VyIiwiaWF0IjoxNzY1NTU1MDk3fQ.o_1S-G4UgjgP_okB_FvOqPWIF5_HOTuv6DW_DCOuSMY";
    private static final Long USER_ID = 1L;
    private static final List<Role> ROLES = List.of(Role.AUTHENTICATED_USER, Role.SIMPLE_USER);

    private final RoleMapper roleMapper = new RoleMapperImpl();

    @Mock
    private TokenProperties tokenProperties;
    @Mock
    private ClockProvider clockProvider;
    @InjectMocks
    private JwtTokenHelper jwtTokenHelper;

    @BeforeEach
    public void initMocks() {
        Mockito.when(tokenProperties.getEncryptionKey()).thenReturn(ENCRYPTION_KEY);
    }

    @Test
    public void parseAccessToken_ShouldThrowExceptionWhenTokenIsMalformed() {
        // GIVEN
        var malformedToken = "malformedToken";
        // THEN
        Assertions.assertThrows(MalformedJwtException.class, () -> jwtTokenHelper.parseAccessToken(malformedToken));
    }

    @Test
    public void parseAccessToken_ShouldThrowExceptionWhenSignatureIsInvalid() {
        Assertions.assertThrows(SignatureException.class, () -> jwtTokenHelper.parseAccessToken(INVALID_TOKEN));
    }

    @Test
    public void parseAccessToken_ShouldThrowExceptionWhenTokenExpired() {
        // GIVEN
        var accessTokenValidityMinutes = 0;
        var clock = Clock.fixed(Instant.now().minusSeconds(1), ZoneOffset.UTC);
        var authorities = getAuthorities();
        Mockito.when(tokenProperties.getAccessTokenValidityMinutes()).thenReturn(accessTokenValidityMinutes);
        Mockito.when(clockProvider.getClock()).thenReturn(clock);
        var token = jwtTokenHelper.generateAccessToken(USER_ID, authorities);
        // THEN
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtTokenHelper.parseAccessToken(token));
    }

    @Test
    public void parseAccessToken_ShouldBuildAuthUserFromValidToken() {
        // GIVEN
        var accessTokenValidityMinutes = 1;
        var clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
        var authorities = getAuthorities();
        Mockito.when(tokenProperties.getAccessTokenValidityMinutes()).thenReturn(accessTokenValidityMinutes);
        Mockito.when(clockProvider.getClock()).thenReturn(clock);
        var token = jwtTokenHelper.generateAccessToken(USER_ID, authorities);
        // WHEN
        var authUser = jwtTokenHelper.parseAccessToken(token);
        // THEN
        Assertions.assertEquals(USER_ID, authUser.getId());
        Assertions.assertEquals(authorities, authUser.getAuthorities());
    }

    @Test
    public void parseRefreshToken_ShouldThrowExceptionWhenTokenIsMalformed() {
        // GIVEN
        var malformedToken = "malformedToken";
        // THEN
        Assertions.assertThrows(MalformedJwtException.class, () -> jwtTokenHelper.parseRefreshToken(malformedToken));
    }

    @Test
    public void parseRefreshToken_ShouldThrowExceptionWhenSignatureIsInvalid() {
        Assertions.assertThrows(SignatureException.class, () -> jwtTokenHelper.parseRefreshToken(INVALID_TOKEN));
    }

    @Test
    public void parseRefreshToken_ShouldThrowExceptionWhenTokenExpired() {
        // GIVEN
        var refreshTokenExpirationDays = 0;
        var clock = Clock.fixed(Instant.now().minusSeconds(1), ZoneOffset.UTC);
        var authorities = getAuthorities();
        Mockito.when(tokenProperties.getRefreshTokenExpirationDays()).thenReturn(refreshTokenExpirationDays);
        Mockito.when(clockProvider.getClock()).thenReturn(clock);
        var token = jwtTokenHelper.generateRefreshToken(USER_ID, authorities);
        // THEN
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtTokenHelper.parseRefreshToken(token));
    }

    @Test
    public void parseRefreshToken_ShouldBuildAuthUserFromValidToken() {
        // GIVEN
        var refreshTokenExpirationDays = 1;
        var clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
        var authorities = getAuthorities();
        Mockito.when(tokenProperties.getRefreshTokenExpirationDays()).thenReturn(refreshTokenExpirationDays);
        Mockito.when(clockProvider.getClock()).thenReturn(clock);
        var token = jwtTokenHelper.generateRefreshToken(USER_ID, authorities);
        // WHEN
        var authUser = jwtTokenHelper.parseRefreshToken(token);
        // THEN
        Assertions.assertEquals(USER_ID, authUser.getId());
        Assertions.assertEquals(authorities, authUser.getAuthorities());
    }

    private List<GrantedAuthority> getAuthorities() {
        return ROLES.stream().map(roleMapper::toAuthority).toList();
    }
}
