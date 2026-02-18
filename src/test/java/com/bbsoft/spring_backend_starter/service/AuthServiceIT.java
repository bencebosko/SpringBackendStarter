package com.bbsoft.spring_backend_starter.service;

import com.bbsoft.spring_backend_starter.IntegrationTestBase;
import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.repository.MailRepository;
import com.bbsoft.spring_backend_starter.repository.UserAuthRepository;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.UserRoleRepository;
import com.bbsoft.spring_backend_starter.repository.UserSettingsRepository;
import com.bbsoft.spring_backend_starter.security.JwtTokenHelper;
import com.bbsoft.spring_backend_starter.service.dto.LoginRequest;
import com.bbsoft.spring_backend_starter.service.dto.user.UserRequest;
import com.bbsoft.spring_backend_starter.service.mapper.RoleMapper;
import com.bbsoft.spring_backend_starter.service.user.UserAuthService;
import com.bbsoft.spring_backend_starter.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class AuthServiceIT extends IntegrationTestBase {

    private static final List<Role> ROLES = List.of(Role.SIMPLE_USER, Role.AUTHENTICATED_USER);

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final int VERIFICATION_CODE = 123456;

    private final AuthService authService;
    private final UserService userService;
    private final UserAuthService userAuthService;
    private final JwtTokenHelper jwtTokenHelper;
    private final RoleMapper roleMapper;

    @Autowired
    public AuthServiceIT(UserRepository userRepository,
                         UserRoleRepository userRoleRepository,
                         UserAuthRepository userAuthRepository,
                         UserSettingsRepository userSettingsRepository,
                         MailRepository mailRepository, AuthService authService, UserService userService, UserAuthService userAuthService, JwtTokenHelper jwtTokenHelper,
                         RoleMapper roleMapper) {
        super(userRepository, userRoleRepository, userAuthRepository, userSettingsRepository, mailRepository);
        this.authService = authService;
        this.userService = userService;
        this.userAuthService = userAuthService;
        this.jwtTokenHelper = jwtTokenHelper;
        this.roleMapper = roleMapper;
    }

    @BeforeEach
    public void initUserRoles() {
        final var userRolesToSave = ROLES.stream().map(roleMapper::toUserRole).collect(Collectors.toList());
        userRoleRepository.saveAll(userRolesToSave);
    }

    @Test
    public void login_AuthenticateVerifyUserAndGenerateBothTokens() {
        // GIVEN
        var userDTO = userService.createSimpleUser(createUserRequest());
        userAuthService.setVerificationCode(userDTO.getId(), VERIFICATION_CODE);
        // WHEN
        var accessTokenResponse = authService.login(createLoginRequest());
        // THEN
        final var userAuth = userAuthRepository.findByUserId(userDTO.getId());
        Assertions.assertTrue(userAuth.isPresent());
        Assertions.assertEquals(accessTokenResponse.getRefreshToken(), userAuth.get().getRefreshToken());
        final var accessTokenUser = jwtTokenHelper.parseAccessToken(accessTokenResponse.getAccessToken());
        Assertions.assertEquals(userDTO.getId(), accessTokenUser.getId());
        Assertions.assertEquals(ROLES.stream().map(roleMapper::toAuthority).toList(), accessTokenUser.getAuthorities());
        final var refreshTokenUser = jwtTokenHelper.parseRefreshToken(accessTokenResponse.getRefreshToken());
        Assertions.assertEquals(userDTO.getId(), refreshTokenUser.getId());
        Assertions.assertEquals(ROLES.stream().map(roleMapper::toAuthority).toList(), refreshTokenUser.getAuthorities());
    }

    private UserRequest createUserRequest() {
        return UserRequest.builder()
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .build();
    }

    private LoginRequest createLoginRequest() {
        return LoginRequest.builder()
            .usernameOrEmail(USERNAME)
            .password(PASSWORD)
            .verificationCode(VERIFICATION_CODE)
            .build();
    }
}
