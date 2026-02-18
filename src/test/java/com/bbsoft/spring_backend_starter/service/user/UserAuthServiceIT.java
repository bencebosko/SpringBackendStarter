package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.IntegrationTestBase;
import com.bbsoft.spring_backend_starter.repository.MailRepository;
import com.bbsoft.spring_backend_starter.repository.UserAuthRepository;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.UserRoleRepository;
import com.bbsoft.spring_backend_starter.repository.UserSettingsRepository;
import com.bbsoft.spring_backend_starter.repository.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAuthServiceIT extends IntegrationTestBase {

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final int VERIFICATION_CODE = 123456;
    private static final String REFRESH_TOKEN = "refreshToken";

    private final UserAuthService userAuthService;

    @Autowired
    public UserAuthServiceIT(UserRepository userRepository,
                             UserRoleRepository userRoleRepository,
                             UserAuthRepository userAuthRepository,
                             UserSettingsRepository userSettingsRepository,
                             MailRepository mailRepository,
                             UserAuthService userAuthService) {
        super(userRepository, userRoleRepository, userAuthRepository, userSettingsRepository, mailRepository);
        this.userAuthService = userAuthService;
    }

    @Test
    public void setVerificationCodeAndVerifyUser() {
        // GIVEN
        var savedUser = userRepository.save(createUser());
        // WHEN
        userAuthService.setVerificationCode(savedUser.getId(), VERIFICATION_CODE);
        userAuthService.verifyUser(savedUser.getId(), VERIFICATION_CODE);
        // THEN
        var savedUserAuth = userAuthRepository.findByUserId(savedUser.getId());
        Assertions.assertTrue(savedUserAuth.isPresent());
        Assertions.assertNull(savedUserAuth.get().getVerificationCode());
    }

    @Test
    public void setRefreshTokenAndVerifyRefreshToken() {
        // GIVEN
        var savedUser = userRepository.save(createUser());
        // WHEN
        userAuthService.setRefreshToken(savedUser.getId(), REFRESH_TOKEN);
        // THEN
        Assertions.assertDoesNotThrow(() -> userAuthService.verifyRefreshToken(savedUser.getId(), REFRESH_TOKEN));
    }

    private User createUser() {
        return User.builder()
            .username(USERNAME)
            .email(EMAIL)
            .encodedPassword(ENCODED_PASSWORD)
            .build();
    }
}
