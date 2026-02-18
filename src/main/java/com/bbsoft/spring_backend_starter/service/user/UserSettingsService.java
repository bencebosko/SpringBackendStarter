package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.exception.EntityNotFoundException;
import com.bbsoft.spring_backend_starter.repository.UserSettingsRepository;
import com.bbsoft.spring_backend_starter.repository.entity.UserSettings;
import com.bbsoft.spring_backend_starter.repository.projection.UserLocale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    public void createDefaultSettings(Long userId) {
        userSettingsRepository.save(getDefaultSettings(userId));
    }

    @Transactional(readOnly = true)
    public Locale findLocaleByUserId(Long userId) {
        return userSettingsRepository.findLocaleByUserId(userId).map(UserLocale::getLocale).orElseThrow(() -> EntityNotFoundException.createUserSettingsNotFound(userId));
    }

    private UserSettings getDefaultSettings(Long userId) {
        return UserSettings.builder()
            .userId(userId)
            .locale(UserSettings.DEFAULT_LOCALE)
            .build();
    }
}
