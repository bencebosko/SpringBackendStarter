package com.bbsoft.spring_backend_starter.security;

import com.bbsoft.spring_backend_starter.exception.SpringBackendException;
import com.bbsoft.spring_backend_starter.service.dto.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class SecurityHelper {

    public void setAuthentication(UsernamePasswordAuthenticationToken authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public AuthenticatedUser getAuthenticatedUser() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || Objects.isNull(authentication.getPrincipal())) {
            throw new SpringBackendException("Authenticated user not found.");
        }
        return (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
