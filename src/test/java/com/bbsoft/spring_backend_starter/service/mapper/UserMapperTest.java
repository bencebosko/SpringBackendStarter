package com.bbsoft.spring_backend_starter.service.mapper;

import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.repository.entity.UserRole;
import com.bbsoft.spring_backend_starter.service.dto.user.UserPatch;
import com.bbsoft.spring_backend_starter.service.dto.user.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final Set<Role> ROLES = Set.of(Role.AUTHENTICATED_USER, Role.SIMPLE_USER);

    @Mock
    private PasswordEncoder passwordEncoder;
    @Spy
    private final RoleMapper roleMapper = new RoleMapperImpl();
    @InjectMocks
    private final UserMapper userMapper = new UserMapperImpl(roleMapper);

    @Test
    public void toUser_ShouldMapNullValuesToNull() {
        Assertions.assertNull(userMapper.toUser(null, null));
    }

    @Test
    public void toUser_ShouldMapNullRolesToNull() {
        Assertions.assertNull(userMapper.toUser(createUserRequest(), null).getRoles());
    }

    @Test
    public void toUser_ShouldMapUserRequestToUser() {
        // GIVEN
        var userRequest = createUserRequest();
        var userRoles = getUserRoles();
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        // WHEN
        var user = userMapper.toUser(userRequest, userRoles);
        // THEN
        Assertions.assertNull(user.getId());
        Assertions.assertEquals(userRequest.getUsername(), user.getUsername());
        Assertions.assertEquals(userRequest.getEmail(), user.getEmail());
        Assertions.assertEquals(ENCODED_PASSWORD, user.getEncodedPassword());
        Assertions.assertEquals(userRequest.getFirstName(), user.getFirstName());
        Assertions.assertEquals(userRequest.getLastName(), user.getLastName());
        Assertions.assertEquals(userRoles, user.getRoles());
    }

    @Test
    public void toUser_ShouldMapNullUserPatchToNull() {
        Assertions.assertNull(userMapper.toUser(null));
    }

    @Test
    public void toUser_ShouldMapUserPatchRequestToUser() {
        // GIVEN
        var userPatchRequest = createUserPatchRequest();
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        // WHEN
        var user = userMapper.toUser(userPatchRequest);
        // THEN
        Assertions.assertNull(user.getId());
        Assertions.assertNull(user.getRoles());
        Assertions.assertNull(user.getVersion());

        Assertions.assertEquals(userPatchRequest.getUsername(), user.getUsername());
        Assertions.assertEquals(userPatchRequest.getEmail(), user.getEmail());
        Assertions.assertEquals(ENCODED_PASSWORD, user.getEncodedPassword());
        Assertions.assertEquals(userPatchRequest.getFirstName(), user.getFirstName());
        Assertions.assertEquals(userPatchRequest.getLastName(), user.getLastName());
    }

    @Test
    public void toUser_ShouldMapNullUserPatchPasswordToNull() {
        // GIVEN
        var userPatchRequest = createUserPatchRequest();
        userPatchRequest.setPassword(null);
        // WHEN
        var userPatch = userMapper.toUser(userPatchRequest);
        // THEN
        Assertions.assertNull(userPatch.getEncodedPassword());
    }

    @Test
    public void toUserDetails_ShouldMapNullUserToNull() {
        Assertions.assertNull(userMapper.toUserDetails(null));
    }

    @Test
    public void toUserDetails_ShouldMapUserToUserDetails() {
        // GIVEN
        var userRoles = getUserRoles();
        var user = userMapper.toUser(createUserRequest(), userRoles);
        var expectedEnabled = true;
        // WHEN
        var userDetails = userMapper.toUserDetails(user);
        // THEN
        Assertions.assertEquals(user.getId(), userDetails.getId());
        Assertions.assertEquals(user.getUsername(), userDetails.getUsername());
        Assertions.assertEquals(user.getEmail(), userDetails.getEmail());
        Assertions.assertEquals(user.getFirstName(), userDetails.getFirstName());
        Assertions.assertEquals(user.getEncodedPassword(), userDetails.getPassword());
        Assertions.assertEquals(expectedEnabled, userDetails.isEnabled());
        Assertions.assertEquals(user.getRoles().stream().map(roleMapper::toAuthority).toList(), userDetails.getAuthorities());
    }

    @Test
    public void toUserDTO_ShouldMapNullUserToNull() {
        Assertions.assertNull(userMapper.toUserDTO(null));
    }

    @Test
    public void toUserDTO_ShouldMapUserToUserDTO() {
        // GIVEN
        var userRoles = getUserRoles();
        var user = userMapper.toUser(createUserRequest(), userRoles);
        // WHEN
        var userDTO = userMapper.toUserDTO(user);
        // THEN
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getEmail(), userDTO.getEmail());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    public void toUserResponse_ShouldMapNullUserToNull() {
        Assertions.assertNull(userMapper.toUserResponse(null));
    }

    @Test
    public void toUserResponse_ShouldMapUserToUserResponse() {
        // GIVEN
        var userRoles = getUserRoles();
        var user = userMapper.toUser(createUserRequest(), userRoles);
        // WHEN
        var userResponse = userMapper.toUserResponse(user);
        // THEN
        Assertions.assertEquals(user.getUsername(), userResponse.getUsername());
        Assertions.assertEquals(user.getEmail(), userResponse.getEmail());
        Assertions.assertEquals(user.getFirstName(), userResponse.getFirstName());
        Assertions.assertEquals(user.getLastName(), userResponse.getLastName());
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

    private UserPatch createUserPatchRequest() {
        return UserPatch.builder()
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .build();
    }

    private List<UserRole> getUserRoles() {
        return ROLES.stream().map(roleMapper::toUserRole).collect(Collectors.toList());
    }
}
