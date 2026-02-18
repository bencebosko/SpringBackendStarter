package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.config.ObjectMapperProvider;
import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.exception.EntityNotFoundException;
import com.bbsoft.spring_backend_starter.exception.UserVerificationException;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.entity.User;
import com.bbsoft.spring_backend_starter.repository.entity.UserRole;
import com.bbsoft.spring_backend_starter.service.dto.user.UserPatch;
import com.bbsoft.spring_backend_starter.service.dto.user.UserRequest;
import com.bbsoft.spring_backend_starter.service.helper.ObjectMerger;
import com.bbsoft.spring_backend_starter.service.mapper.RoleMapperImpl;
import com.bbsoft.spring_backend_starter.service.mapper.UserMapper;
import com.bbsoft.spring_backend_starter.service.mapper.UserMapperImpl;
import com.bbsoft.spring_backend_starter.service.mapper.RoleMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final Long USER_ID = 1L;
    private static final Set<Role> SIMPLE_USER_ROLES = Set.of(Role.AUTHENTICATED_USER, Role.SIMPLE_USER);

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

    @Spy
    private UserRepository userRepository;
    @Spy
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private UserRoleService userRoleService;
    private final RoleMapper roleMapper = new RoleMapperImpl();
    @Spy
    private final UserMapper userMapper = new UserMapperImpl(roleMapper);
    private final ObjectMapperProvider objectMapperProvider = new ObjectMapperProvider();
    @Spy
    private final ObjectMerger objectMerger = new ObjectMerger(objectMapperProvider.getObjectMapper());
    @Mock
    private UserSettingsService userSettingsService;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void initMocks() {
        userMapper.setPasswordEncoder(passwordEncoder);
    }

    @Test
    public void createUserWithRoles_ShouldCreateUserWithRoles() {
        // GIVEN
        var userRequest = createUserRequest();
        var savedUser = createSimpleUser();
        Mockito.when(userRoleService.collectUserRoles(SIMPLE_USER_ROLES)).thenReturn(savedUser.getRoles());
        Mockito.when(userRepository.save(any())).thenReturn(savedUser);
        // WHEN
        var userDTO = userService.createUserWithRoles(userRequest, SIMPLE_USER_ROLES);
        // THEN
        Mockito.verify(userMapper, times(1)).toUser(userRequest, savedUser.getRoles());
        Mockito.verify(userSettingsService, times(1)).createDefaultSettings(USER_ID);
        Assertions.assertEquals(savedUser.getId(), userDTO.getId());
        Assertions.assertEquals(userRequest.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(userRequest.getEmail(), userDTO.getEmail());
        Assertions.assertEquals(userRequest.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(userRequest.getLastName(), userDTO.getLastName());
        Assertions.assertTrue(passwordEncoder.matches(userRequest.getPassword(), savedUser.getEncodedPassword()));
    }

    @Test
    public void createSimpleUser_ShouldCallCreateUserWithRoles() {
        // GIVEN
        var userRequest = createUserRequest();
        var savedUser = createSimpleUser();
        var simpleUserRoles = getSimpleUserRoles();
        Mockito.when(userRoleService.collectUserRoles(SIMPLE_USER_ROLES)).thenReturn(simpleUserRoles);
        Mockito.when(userRepository.save(any())).thenReturn(savedUser);
        // WHEN
        userService.createSimpleUser(userRequest);
        // THEN
        Mockito.verify(userMapper, times(1)).toUser(userRequest, simpleUserRoles);
        Mockito.verify(userSettingsService, times(1)).createDefaultSettings(USER_ID);
    }

    @Test
    public void patchUser_ShouldThrowExceptionWhenUserNotFound() {
        // GIVEN
        var userPatch = createUserPatch();
        Mockito.when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        // THEN
        var thrownException = Assertions.assertThrows(EntityNotFoundException.class, () -> userService.patchUser(USERNAME, userPatch));
        Assertions.assertEquals(ErrorCodes.ENTITY_NOT_FOUND, thrownException.getErrorCode());
    }

    @Test
    public void patchUser_ShouldPatchAndSaveUser() {
        // GIVEN
        var userPatch = createUserPatch();
        var savedUser = createSimpleUser();
        var patchedUser = createPatchedSimpleUser();
        Mockito.when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(savedUser));
        Mockito.when(userRepository.save(patchedUser)).thenReturn(patchedUser);
        // WHEN
        var userDTO = userService.patchUser(USERNAME, userPatch);
        // THEN
        Mockito.verify(userRepository, times(1)).save(patchedUser);
        Assertions.assertEquals(userPatch.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(userPatch.getEmail(), userDTO.getEmail());
        Assertions.assertTrue(passwordEncoder.matches(userPatch.getPassword(), patchedUser.getEncodedPassword()));
        Assertions.assertEquals(userPatch.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(userPatch.getLastName(), userDTO.getLastName());
    }

    @Test
    public void deleteUserByUsername_ShouldDeleteUserByUsername() {
        // WHEN
        userService.deleteUserByUsername(USERNAME);
        // THEN
        Mockito.verify(userRepository, times(1)).deleteByUsername(USERNAME);
    }

    @Test
    public void confirmPassword_ShouldThrowExceptionWhenUserNotFound() {
        // GIVEN
        Mockito.when(userRepository.findUserPasswordById(USER_ID)).thenReturn(Optional.empty());
        // THEN
        var thrownException = Assertions.assertThrows(EntityNotFoundException.class, () -> userService.confirmPassword(USER_ID, PASSWORD));
        Assertions.assertEquals(ErrorCodes.ENTITY_NOT_FOUND, thrownException.getErrorCode());
    }

    @Test
    public void confirmPassword_ShouldThrowExceptionIfPasswordNotMatch() {
        // GIVEN
        var notMatchingPassword = getEncodedPassword("notMatchingPassword");
        Mockito.when(userRepository.findUserPasswordById(USER_ID)).thenReturn(Optional.of(() -> notMatchingPassword));
        // THEN
        var thrownException = Assertions.assertThrows(UserVerificationException.class, () -> userService.confirmPassword(USER_ID, PASSWORD));
        Assertions.assertEquals(ErrorCodes.INVALID_CONFIRMATION_PASSWORD, thrownException.getErrorCode());
    }

    @Test
    public void confirmPassword_ShouldNotThrowExceptionIfPasswordMatch() {
        // GIVEN
        var matchingPassword = getEncodedPassword(PASSWORD);
        Mockito.when(userRepository.findUserPasswordById(USER_ID)).thenReturn(Optional.of(() -> matchingPassword));
        // THEN
        Assertions.assertDoesNotThrow(() -> userService.confirmPassword(USER_ID, PASSWORD));
    }

    @Test
    public void findUserById_ShouldThrowExceptionIfNotFound() {
        // GIVEN
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        // THEN
        var thrownException = Assertions.assertThrows(EntityNotFoundException.class, () -> userService.findUserById(USER_ID));
        Assertions.assertEquals(ErrorCodes.ENTITY_NOT_FOUND, thrownException.getErrorCode());
    }

    @Test
    public void findUserById_ShouldFindTheUser() {
        // GIVEN
        var savedUser = createSimpleUser();
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(savedUser));
        // WHEN
        var foundUser = userService.findUserById(USER_ID);
        Assertions.assertEquals(savedUser.getUsername(), foundUser.getUsername());
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

    private User createSimpleUser() {
        return User.builder()
            .id(USER_ID)
            .username(USERNAME)
            .email(EMAIL)
            .encodedPassword(getEncodedPassword(PASSWORD))
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .roles(getSimpleUserRoles())
            .build();
    }

    private User createPatchedSimpleUser() {
        final var patchedUser = createSimpleUser();
        patchedUser.setUsername(USERNAME_PATCH);
        patchedUser.setEmail(EMAIL_PATCH);
        patchedUser.setEncodedPassword(getEncodedPassword(PASSWORD_PATCH));
        patchedUser.setFirstName(FIRST_NAME_PATCH);
        patchedUser.setLastName(LAST_NAME_PATCH);
        return patchedUser;
    }

    private String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private List<UserRole> getSimpleUserRoles() {
        return SIMPLE_USER_ROLES.stream().map(roleMapper::toUserRole).toList();
    }
}
