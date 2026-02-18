package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.IntegrationTestBase;
import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.exception.EntityNotFoundException;
import com.bbsoft.spring_backend_starter.repository.MailRepository;
import com.bbsoft.spring_backend_starter.repository.UserAuthRepository;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.UserRoleRepository;
import com.bbsoft.spring_backend_starter.repository.UserSettingsRepository;
import com.bbsoft.spring_backend_starter.repository.entity.UserSettings;
import com.bbsoft.spring_backend_starter.service.dto.user.UserPatch;
import com.bbsoft.spring_backend_starter.service.dto.user.UserRequest;
import com.bbsoft.spring_backend_starter.service.mapper.RoleMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class UserServiceIT extends IntegrationTestBase {

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    private static final String USERNAME_PATCH = "usernamePatch";
    private static final String EMAIL_PATCH = "emailPatch";
    private static final String PASSWORD_PATCH = "passwordPatch";
    private static final String FIRST_NAME_PATCH = "firstNamePatch";
    private static final String LAST_NAME_PATCH = "lastNamePatch";

    private static final List<Role> ROLES = List.of(Role.SIMPLE_USER, Role.AUTHENTICATED_USER);

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserSettingsService userSettingsService;
    private final RoleMapper roleMapper;

    @Autowired
    public UserServiceIT(UserRepository userRepository,
                         UserRoleRepository userRoleRepository,
                         UserAuthRepository userAuthRepository,
                         UserSettingsRepository userSettingsRepository,
                         MailRepository mailRepository,
                         UserService userService,
                         UserDetailsServiceImpl userDetailsService,
                         UserSettingsService userSettingsService,
                         RoleMapper roleMapper) {
        super(userRepository, userRoleRepository, userAuthRepository, userSettingsRepository, mailRepository);
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.userSettingsService = userSettingsService;
        this.roleMapper = roleMapper;
    }

    @BeforeEach
    public void initUserRoles() {
        var userRolesToSave = ROLES.stream().map(roleMapper::toUserRole).collect(Collectors.toList());
        userRoleRepository.saveAll(userRolesToSave);
    }

    @Test
    public void createUserAndLoadByUsername() {
        // GIVEN
        var userRequest = createUserRequest();
        // WHEN
        userService.createUserWithRoles(userRequest, new HashSet<>(ROLES));
        var userDetails = userDetailsService.loadUserByUsername(userRequest.getUsername());
        // THEN
        Assertions.assertEquals(UserSettings.DEFAULT_LOCALE, userSettingsService.findLocaleByUserId(userDetails.getId()));
        Assertions.assertEquals(userRequest.getUsername(), userDetails.getUsername());
    }

    @Test
    public void patchUserAndConfirmPassword() {
        // GIVEN
        var userRequest = createUserRequest();
        var userPatchRequest = createUserPatch();
        userService.createUserWithRoles(userRequest, new HashSet<>(ROLES));
        // WHEN
        var userDTO = userService.patchUser(userRequest.getUsername(), userPatchRequest);
        // THEN
        userService.confirmPassword(userDTO.getId(), userPatchRequest.getPassword());
        Assertions.assertEquals(userPatchRequest.getEmail(), userDTO.getEmail());
        Assertions.assertEquals(userPatchRequest.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(userPatchRequest.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(userPatchRequest.getLastName(), userDTO.getLastName());
    }

    @Test
    public void deleteUserAndFindById() {
        // GIVEN
        var userRequest = createUserRequest();
        var userDTO = userService.createUserWithRoles(userRequest, new HashSet<>(ROLES));
        // WHEN
        userService.deleteUserByUsername(userRequest.getUsername());
        // THEN
        var thrownException = Assertions.assertThrows(EntityNotFoundException.class, () -> userService.findUserById(userDTO.getId()));
        Assertions.assertEquals(ErrorCodes.ENTITY_NOT_FOUND, thrownException.getErrorCode());
    }

    private UserRequest createUserRequest() {
        return UserRequest.builder()
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .build();
    }

    private UserPatch createUserPatch() {
        return UserPatch.builder()
            .username(USERNAME_PATCH)
            .email(EMAIL_PATCH)
            .password(PASSWORD_PATCH)
            .firstName(FIRST_NAME_PATCH)
            .lastName(LAST_NAME_PATCH)
            .build();
    }
}
