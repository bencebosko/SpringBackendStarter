package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import com.bbsoft.spring_backend_starter.exception.EntityNotFoundException;
import com.bbsoft.spring_backend_starter.exception.UserVerificationException;
import com.bbsoft.spring_backend_starter.repository.UserAuthRepository;
import com.bbsoft.spring_backend_starter.repository.entity.UserAuth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserAuthServiceTest {

    private static final Long USER_ID = 1L;
    private static final int VERIFICATION_CODE = 123456;
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String NEW_REFRESH_TOKEN = "newRefreshToken";

    @Mock
    private UserAuthRepository userAuthRepository;
    @InjectMocks
    private UserAuthService userAuthService;

    @Test
    public void setVerificationCode_ShouldCreateUserAuthIfNotFound() {
        // GIVEN
        Mockito.when(userAuthRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        // WHEN
        userAuthService.setVerificationCode(USER_ID, VERIFICATION_CODE);
        // THEN
        var userAuthCaptor = ArgumentCaptor.forClass(UserAuth.class);
        Mockito.verify(userAuthRepository, times(1)).save(userAuthCaptor.capture());
        Assertions.assertEquals(USER_ID, userAuthCaptor.getValue().getUserId());
        Assertions.assertEquals(VERIFICATION_CODE, userAuthCaptor.getValue().getVerificationCode());
    }

    @Test
    public void setVerificationCode_ShouldUpdateVerificationCode() {
        // GIVEN
        var savedUserAuth = createUserAuth();
        var newVerificationCode = 234567;
        Mockito.when(userAuthRepository.findByUserId(USER_ID)).thenReturn(Optional.of(savedUserAuth));
        // WHEN
        userAuthService.setVerificationCode(USER_ID, newVerificationCode);
        // THEN
        var userAuthCaptor = ArgumentCaptor.forClass(UserAuth.class);
        Mockito.verify(userAuthRepository, times(1)).save(userAuthCaptor.capture());
        Assertions.assertEquals(USER_ID, userAuthCaptor.getValue().getUserId());
        Assertions.assertEquals(newVerificationCode, userAuthCaptor.getValue().getVerificationCode());
    }

    @Test
    public void verifyUser_ShouldThrowExceptionWhenUserAuthNotFound() {
        // GIVEN
        Mockito.when(userAuthRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        // THEN
        var thrownException = Assertions.assertThrows(EntityNotFoundException.class, () -> userAuthService.verifyUser(USER_ID, VERIFICATION_CODE));
        Assertions.assertEquals(ErrorCodes.ENTITY_NOT_FOUND, thrownException.getErrorCode());
    }

    @Test
    public void verifyUser_ShouldThrowExceptionIfVerificationCodeNotMatch() {
        // GIVEN
        var savedUserAuth = createUserAuth();
        savedUserAuth.setVerificationCode(0);
        Mockito.when(userAuthRepository.findByUserId(USER_ID)).thenReturn(Optional.of(savedUserAuth));
        // THEN
        var thrownException = Assertions.assertThrows(UserVerificationException.class, () -> userAuthService.verifyUser(USER_ID, VERIFICATION_CODE));
        Assertions.assertEquals(ErrorCodes.INVALID_VERIFICATION_CODE, thrownException.getErrorCode());
    }

    @Test
    public void verifyUser_ShouldSetNullIfVerificationCodeMatch() {
        // GIVEN
        var savedUserAuth = createUserAuth();
        Mockito.when(userAuthRepository.findByUserId(USER_ID)).thenReturn(Optional.of(savedUserAuth));
        // WHEN
        userAuthService.verifyUser(USER_ID, VERIFICATION_CODE);
        // THEN
        var userAuthCaptor = ArgumentCaptor.forClass(UserAuth.class);
        Mockito.verify(userAuthRepository, times(1)).save(userAuthCaptor.capture());
        Assertions.assertEquals(USER_ID, userAuthCaptor.getValue().getUserId());
        Assertions.assertNull(userAuthCaptor.getValue().getVerificationCode());
    }

    @Test
    public void setRefreshToken_ShouldCreateUserAuthIfNotFound() {
        // GIVEN
        Mockito.when(userAuthRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        // WHEN
        userAuthService.setRefreshToken(USER_ID, NEW_REFRESH_TOKEN);
        // THEN
        var userAuthCaptor = ArgumentCaptor.forClass(UserAuth.class);
        Mockito.verify(userAuthRepository, times(1)).save(userAuthCaptor.capture());
        Assertions.assertEquals(USER_ID, userAuthCaptor.getValue().getUserId());
        Assertions.assertEquals(NEW_REFRESH_TOKEN, userAuthCaptor.getValue().getRefreshToken());
    }

    @Test
    public void setRefreshToken_ShouldSetRefreshTokenAndSave() {
        // GIVEN
        var savedUserAuth = createUserAuth();
        Mockito.when(userAuthRepository.findByUserId(USER_ID)).thenReturn(Optional.of(savedUserAuth));
        // WHEN
        userAuthService.setRefreshToken(USER_ID, NEW_REFRESH_TOKEN);
        // THEN
        var userAuthCaptor = ArgumentCaptor.forClass(UserAuth.class);
        Mockito.verify(userAuthRepository, times(1)).save(userAuthCaptor.capture());
        Assertions.assertEquals(USER_ID, userAuthCaptor.getValue().getUserId());
        Assertions.assertEquals(NEW_REFRESH_TOKEN, userAuthCaptor.getValue().getRefreshToken());
    }

    @Test
    public void verifyRefreshToken_ShouldThrowExceptionWhenUserAuthNotFound() {
        // GIVEN
        Mockito.when(userAuthRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        // THEN
        var thrownException = Assertions.assertThrows(EntityNotFoundException.class, () -> userAuthService.verifyRefreshToken(USER_ID, REFRESH_TOKEN));
        Assertions.assertEquals(ErrorCodes.ENTITY_NOT_FOUND, thrownException.getErrorCode());
    }

    @Test
    public void verifyRefreshToken_ShouldThrowExceptionIfRefreshTokenNotMatch() {
        // GIVEN
        var savedUserAuth = createUserAuth();
        savedUserAuth.setRefreshToken(NEW_REFRESH_TOKEN);
        Mockito.when(userAuthRepository.findByUserId(USER_ID)).thenReturn(Optional.of(savedUserAuth));
        // THEN
        var thrownException = Assertions.assertThrows(UserVerificationException.class, () -> userAuthService.verifyRefreshToken(USER_ID, REFRESH_TOKEN));
        Assertions.assertEquals(ErrorCodes.INVALID_REFRESH_TOKEN, thrownException.getErrorCode());
    }

    @Test
    public void verifyRefreshToken_ShouldDoesNotThrowExceptionIfRefreshTokenMatch() {
        // GIVEN
        var savedUserAuth = createUserAuth();
        Mockito.when(userAuthRepository.findByUserId(USER_ID)).thenReturn(Optional.of(savedUserAuth));
        // THEN
        Assertions.assertDoesNotThrow(() -> userAuthService.verifyRefreshToken(USER_ID, REFRESH_TOKEN));
    }

    private UserAuth createUserAuth() {
        return UserAuth.builder()
            .userId(USER_ID)
            .verificationCode(VERIFICATION_CODE)
            .refreshToken(REFRESH_TOKEN)
            .build();
    }
}
