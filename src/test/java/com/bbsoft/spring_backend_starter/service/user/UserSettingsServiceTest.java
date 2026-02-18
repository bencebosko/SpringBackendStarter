package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import com.bbsoft.spring_backend_starter.exception.EntityNotFoundException;
import com.bbsoft.spring_backend_starter.repository.UserSettingsRepository;
import com.bbsoft.spring_backend_starter.repository.entity.UserSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Locale;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class UserSettingsServiceTest {

    private static final long USER_ID = 1;

    @Mock
    private UserSettingsRepository userSettingsRepository;
    @InjectMocks
    private UserSettingsService userSettingsService;

    @Test
    public void createDefaultSettings_ShouldCreateDefaultSettings() {
        // GIVEN
        var defaultSettings = UserSettings.builder()
            .userId(USER_ID)
            .locale(UserSettings.DEFAULT_LOCALE)
            .build();
        // WHEN
        userSettingsService.createDefaultSettings(USER_ID);
        // THEN
        var settingsCaptor = ArgumentCaptor.forClass(UserSettings.class);
        Mockito.verify(userSettingsRepository, Mockito.times(1)).save(settingsCaptor.capture());
        Assertions.assertEquals(defaultSettings.getUserId(), settingsCaptor.getValue().getUserId());
        Assertions.assertEquals(defaultSettings.getLocale(), settingsCaptor.getValue().getLocale());
    }

    @Test
    public void findLocaleByUserId_ShouldThrowExceptionIfNotFound() {
        // GIVEN
        Mockito.when(userSettingsRepository.findLocaleByUserId(USER_ID)).thenReturn(Optional.empty());
        // THEN
        var thrownException = Assertions.assertThrows(EntityNotFoundException.class, () -> userSettingsService.findLocaleByUserId(USER_ID));
        Assertions.assertEquals(ErrorCodes.ENTITY_NOT_FOUND, thrownException.getErrorCode());
    }

    @Test
    public void findLocaleByUserId_ShouldFindTheLocale() {
        // GIVEN
        var expectedLocale = Locale.UK;
        Mockito.when(userSettingsRepository.findLocaleByUserId(USER_ID)).thenReturn(Optional.of(() -> expectedLocale));
        // THEN
        Assertions.assertEquals(expectedLocale, userSettingsService.findLocaleByUserId(USER_ID));
    }
}
