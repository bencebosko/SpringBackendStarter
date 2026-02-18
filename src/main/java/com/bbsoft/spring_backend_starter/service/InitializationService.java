package com.bbsoft.spring_backend_starter.service;

import com.bbsoft.spring_backend_starter.config.properties.SpringBackendProperties;
import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.service.dto.user.UserRequest;
import com.bbsoft.spring_backend_starter.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InitializationService {

    public static final String DEFAULT_FIRST_NAME = "User";

    private final UserRepository userRepository;
    private final UserService userService;
    private final SpringBackendProperties springBackendProperties;

    public void createDefaultUserIfNotExists() {
        final var defaultUser = userRepository.findByUsername(springBackendProperties.getDefaultUser());
        if (defaultUser.isEmpty()) {
            var defaultUserRequest = UserRequest.builder()
                .username(springBackendProperties.getDefaultUser())
                .password(springBackendProperties.getDefaultPassword())
                .email(springBackendProperties.getDefaultEmail())
                .firstName(DEFAULT_FIRST_NAME)
                .build();
            var defaultRoles = Set.of(Role.AUTHENTICATED_USER, Role.ADMIN);
            userService.createUserWithRoles(defaultUserRequest, defaultRoles);
            log.info("Default user created. {}", defaultRoles);
        }
    }
}
