package com.bbsoft.spring_backend_starter.security;

import com.bbsoft.spring_backend_starter.config.providers.ClockProvider;
import com.bbsoft.spring_backend_starter.config.properties.TokenProperties;
import com.bbsoft.spring_backend_starter.service.dto.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenHelper {

    public static final String USER_ID = "id";
    public static final String USER_AUTHORITIES = "authorities";
    private static final String FIELD_AUTHORITY = "authority";

    private final TokenProperties tokenProperties;
    private final ClockProvider clockProvider;

    public String generateAccessToken(Long userId, List<GrantedAuthority> authorities) {
        final var claims = Map.of(USER_ID, userId.toString(), USER_AUTHORITIES, authorities);
        return createToken(claims, tokenProperties.getAccessTokenValidityMinutes() * 60L);
    }

    public String generateRefreshToken(Long userId, List<GrantedAuthority> authorities) {
        final var claims = Map.of(USER_ID, userId.toString(), USER_AUTHORITIES, authorities);
        return createToken(claims, tokenProperties.getRefreshTokenExpirationDays() * (24L * 60 * 60));
    }

    public AuthenticatedUser parseAccessToken(String token) throws JwtException {
        return parseAccessOrRefreshToken(token);
    }

    public AuthenticatedUser parseRefreshToken(String token) throws JwtException {
        return parseAccessOrRefreshToken(token);
    }

    private String createToken(Map<String, Object> claims, Long expirationSeconds) {
        return Jwts.builder()
            .claims(claims)
            .issuedAt(Date.from(Instant.now(clockProvider.getClock())))
            .expiration(Date.from(Instant.now(clockProvider.getClock()).plusSeconds(expirationSeconds)))
            .signWith(getSignInKey())
            .compact();
    }

    @SuppressWarnings("unchecked")
    private AuthenticatedUser parseAccessOrRefreshToken(String token) {
        final var claims = parseClaimsIfValid(token);
        final List<GrantedAuthority> authorities = ((List<Map<String, String>>) claims.get(USER_AUTHORITIES)).stream()
            .map(obj -> new SimpleGrantedAuthority(obj.get(FIELD_AUTHORITY)))
            .collect(Collectors.toList());
        return AuthenticatedUser.builder()
            .id(Long.valueOf((String) claims.get(USER_ID)))
            .authorities(authorities)
            .build();
    }

    private Claims parseClaimsIfValid(String token) throws JwtException {
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSignInKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(tokenProperties.getEncryptionKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
