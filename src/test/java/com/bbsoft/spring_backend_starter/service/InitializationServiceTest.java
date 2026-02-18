package com.bbsoft.spring_backend_starter.service;

import com.bbsoft.spring_backend_starter.config.properties.SpringBackendProperties;
import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.entity.User;
import com.bbsoft.spring_backend_starter.service.dto.user.UserRequest;
import com.bbsoft.spring_backend_starter.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class InitializationServiceTest {

    private static final String DEFAULT_USERNAME = "defaultUser";
    private static final String DEFAULT_PASSWORD = "defaultPassword";
    private static final String DEFAULT_EMAIL = "defaultEmail";
    private static final Set<Role> DEFAULT_USER_ROLES = Set.of(Role.AUTHENTICATED_USER, Role.ADMIN);

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private SpringBackendProperties springBackendProperties;
    @InjectMocks
    private InitializationService initializationService;

    @BeforeEach
    public void initMocks() {
        Mockito.lenient().when(springBackendProperties.getDefaultUser()).thenReturn(DEFAULT_USERNAME);
        Mockito.lenient().when(springBackendProperties.getDefaultPassword()).thenReturn(DEFAULT_PASSWORD);
        Mockito.lenient().when(springBackendProperties.getDefaultEmail()).thenReturn(DEFAULT_EMAIL);
    }

    @Test
    public void createDefaultUserIfNotExists_ShouldCreateDefaultUserIfNotExists() {
        // GIVEN
        Mockito.when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());
        // WHEN
        initializationService.createDefaultUserIfNotExists();
        // THEN
        ArgumentCaptor<UserRequest> userRequestCaptor = ArgumentCaptor.forClass(UserRequest.class);
        Mockito.verify(userService, Mockito.times(1)).createUserWithRoles(userRequestCaptor.capture(), eq(DEFAULT_USER_ROLES));
        Assertions.assertEquals(DEFAULT_USERNAME, userRequestCaptor.getValue().getUsername());
        Assertions.assertEquals(DEFAULT_PASSWORD, userRequestCaptor.getValue().getPassword());
        Assertions.assertEquals(DEFAULT_EMAIL, userRequestCaptor.getValue().getEmail());
        Assertions.assertEquals(InitializationService.DEFAULT_FIRST_NAME, userRequestCaptor.getValue().getFirstName());
    }

    @Test
    public void createDefaultUserIfNotExists_ShouldDoNothingIfDefaultUserExists() {
        // GIVEN
        Mockito.when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(User.builder().build()));
        // WHEN
        initializationService.createDefaultUserIfNotExists();
        // THEN
        var userRequestCaptor = ArgumentCaptor.forClass(UserRequest.class);
        Mockito.verify(userService, Mockito.times(0)).createUserWithRoles(userRequestCaptor.capture(), eq(DEFAULT_USER_ROLES));
    }
}
