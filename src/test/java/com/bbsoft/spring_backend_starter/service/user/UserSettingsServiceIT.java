package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.IntegrationTestBase;
import com.bbsoft.spring_backend_starter.repository.MailRepository;
import com.bbsoft.spring_backend_starter.repository.UserAuthRepository;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.UserRoleRepository;
import com.bbsoft.spring_backend_starter.repository.UserSettingsRepository;
import com.bbsoft.spring_backend_starter.repository.entity.User;
import com.bbsoft.spring_backend_starter.repository.entity.UserSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public class UserSettingsServiceIT extends IntegrationTestBase {

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    private final UserSettingsService userSettingsService;

    @Autowired
    public UserSettingsServiceIT(UserRepository userRepository,
                                 UserRoleRepository userRoleRepository,
                                 UserAuthRepository userAuthRepository,
                                 UserSettingsRepository userSettingsRepository,
                                 MailRepository mailRepository,
                                 UserSettingsService userSettingsService) {
        super(userRepository, userRoleRepository, userAuthRepository, userSettingsRepository, mailRepository);
        this.userSettingsService = userSettingsService;
    }

    @Test
    public void createDefaultSettingsAndFindLocale() {
        // GIVEN
        var savedUser = userRepository.save(createUser());
        var expectedLocale = Locale.UK;
        // WHEN
        userSettingsRepository.save(UserSettings.builder().userId(savedUser.getId()).locale(Locale.UK).build());
        // THEN
        var locale = userSettingsService.findLocaleByUserId(savedUser.getId());
        Assertions.assertEquals(expectedLocale, locale);
    }

    public User createUser() {
        return User.builder()
            .username(USERNAME)
            .email(EMAIL)
            .encodedPassword(ENCODED_PASSWORD)
            .build();
    }
}
