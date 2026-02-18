package com.bbsoft.spring_backend_starter.service;

import com.bbsoft.spring_backend_starter.config.properties.SpringBackendProperties;
import com.bbsoft.spring_backend_starter.constant.MailType;
import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.constant.TemplateVariables;
import com.bbsoft.spring_backend_starter.security.JwtTokenHelper;
import com.bbsoft.spring_backend_starter.service.dto.AuthRequest;
import com.bbsoft.spring_backend_starter.service.dto.AuthenticatedUser;
import com.bbsoft.spring_backend_starter.service.dto.LoginRequest;
import com.bbsoft.spring_backend_starter.service.dto.UserAuthDetails;
import com.bbsoft.spring_backend_starter.service.dto.mail.SendMailDTO;
import com.bbsoft.spring_backend_starter.service.helper.VerificationCodeGenerator;
import com.bbsoft.spring_backend_starter.service.mail.SendMailFactory;
import com.bbsoft.spring_backend_starter.service.mail.MailService;
import com.bbsoft.spring_backend_starter.service.user.UserAuthService;
import com.bbsoft.spring_backend_starter.service.user.UserSettingsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "firstName";
    private static final List<GrantedAuthority> AUTHORITIES = List.of(new SimpleGrantedAuthority(Role.SIMPLE_USER.getName()));

    private static final Long USER_ID = 1L;
    private static final Locale LOCALE = Locale.US;
    private static final int VERIFICATION_CODE = 123456;

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AuthenticationManager authenticationManager;
    @Mock
    private UserAuthService userAuthService;
    @Mock
    private UserSettingsService userSettingsService;
    @Mock
    private MailService mailService;
    @Spy
    private SendMailFactory sendMailFactory;
    @Mock
    private VerificationCodeGenerator verificationCodeGenerator;
    @Mock
    private JwtTokenHelper jwtTokenHelper;
    @Mock
    private SpringBackendProperties springBackendProperties;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void initMocks() {
        Mockito.lenient().when(verificationCodeGenerator.generate()).thenReturn(VERIFICATION_CODE);
        Mockito.lenient().when(jwtTokenHelper.generateAccessToken(USER_ID, AUTHORITIES)).thenReturn(ACCESS_TOKEN);
        Mockito.lenient().when(jwtTokenHelper.generateRefreshToken(USER_ID, AUTHORITIES)).thenReturn(REFRESH_TOKEN);
    }

    @Test
    public void authenticate_ShouldNotAuthenticateIfLoginVerificationNotEnabled() {
        // GIVEN
        var loginVerificationEnabled = false;
        Mockito.when(springBackendProperties.getLoginVerificationEnabled()).thenReturn(loginVerificationEnabled);
        // WHEN
        var authResponse = authService.authenticate(createAuthRequest());
        // THEN
        Assertions.assertFalse(authResponse.isLoginVerificationEnabled());
    }

    @Test
    public void authenticate_ShouldSetVerificationCodeAndSaveMail() {
        // GIVEN
        var authenticationToken = createAuthenticationToken();
        var userAuthDetails = createUserAuthDetails();
        var loginVerificationEnabled = true;
        var expectedTemplateVariables = Map.of(
            TemplateVariables.USERNAME, USERNAME,
            TemplateVariables.VERIFICATION_CODE, VERIFICATION_CODE);
        var expectedSubjectReplacements = new Object[]{VERIFICATION_CODE};
        Mockito.when(springBackendProperties.getLoginVerificationEnabled()).thenReturn(loginVerificationEnabled);
        Mockito.when(authenticationManager.authenticate(authenticationToken).getPrincipal()).thenReturn(userAuthDetails);
        Mockito.when(userSettingsService.findLocaleByUserId(USER_ID)).thenReturn(LOCALE);
        // WHEN
        var authResponse = authService.authenticate(createAuthRequest());
        // THEN
        var sendMailDTOCaptor = ArgumentCaptor.forClass(SendMailDTO.class);
        Mockito.verify(userAuthService, Mockito.times(1)).setVerificationCode(USER_ID, VERIFICATION_CODE);
        Mockito.verify(mailService, Mockito.times(1)).saveMail(sendMailDTOCaptor.capture());
        final var sendMailDTO = sendMailDTOCaptor.getValue();
        Assertions.assertEquals(MailType.LOGIN_VERIFICATION, sendMailDTO.getMailType());
        Assertions.assertEquals(EMAIL, sendMailDTO.getToAddress());
        Assertions.assertEquals(FIRST_NAME, sendMailDTO.getFirstName());
        Assertions.assertEquals(LOCALE, sendMailDTO.getLocale());
        Assertions.assertEquals(expectedTemplateVariables, sendMailDTO.getTemplateVariables());
        Assertions.assertEquals(expectedSubjectReplacements[0], sendMailDTO.getSubjectReplacements()[0]);
        Assertions.assertTrue(authResponse.isLoginVerificationEnabled());
    }

    @Test
    public void login_ShouldVerifyUserAndGenerateBothTokens() {
        // GIVEN
        var authenticationToken = createAuthenticationToken();
        var userAuthDetails = createUserAuthDetails();
        var loginVerificationEnabled = true;
        Mockito.when(authenticationManager.authenticate(authenticationToken).getPrincipal()).thenReturn(userAuthDetails);
        Mockito.when(springBackendProperties.getLoginVerificationEnabled()).thenReturn(loginVerificationEnabled);
        // WHEN
        var accessTokenResponse = authService.login(createLoginRequest());
        // THEN
        Mockito.verify(userAuthService, Mockito.times(1)).verifyUser(USER_ID, VERIFICATION_CODE);
        Mockito.verify(userAuthService, Mockito.times(1)).setRefreshToken(USER_ID, REFRESH_TOKEN);
        Assertions.assertEquals(ACCESS_TOKEN, accessTokenResponse.getAccessToken());
        Assertions.assertEquals(REFRESH_TOKEN, accessTokenResponse.getRefreshToken());
    }

    @Test
    public void login_ShouldNotVerifyUserAndGenerateBothTokens() {
        // GIVEN
        var authenticationToken = createAuthenticationToken();
        var userAuthDetails = createUserAuthDetails();
        var loginVerificationEnabled = false;
        Mockito.when(authenticationManager.authenticate(authenticationToken).getPrincipal()).thenReturn(userAuthDetails);
        Mockito.when(springBackendProperties.getLoginVerificationEnabled()).thenReturn(loginVerificationEnabled);
        // WHEN
        var accessTokenResponse = authService.login(createLoginRequest());
        // THEN
        Mockito.verify(userAuthService, Mockito.times(0)).verifyUser(USER_ID, VERIFICATION_CODE);
        Mockito.verify(userAuthService, Mockito.times(1)).setRefreshToken(USER_ID, REFRESH_TOKEN);
        Assertions.assertEquals(ACCESS_TOKEN, accessTokenResponse.getAccessToken());
        Assertions.assertEquals(REFRESH_TOKEN, accessTokenResponse.getRefreshToken());
    }

    @Test
    public void logout_ShouldSetRefreshTokenToNull() {
        // WHEN
        authService.logout(USER_ID);
        // THEN
        Mockito.verify(userAuthService, Mockito.times(1)).setRefreshToken(USER_ID, null);
    }

    @Test
    public void refreshAccessToken_ShouldVerifyRefreshTokenAndGenerateAccessToken() {
        // WHEN
        var accessToken = authService.refreshAccessToken(createAuthenticatedUser(), REFRESH_TOKEN);
        // THEN
        Mockito.verify(userAuthService, Mockito.times(1)).verifyRefreshToken(USER_ID, REFRESH_TOKEN);
        Assertions.assertEquals(ACCESS_TOKEN, accessToken);
    }

    private AuthRequest createAuthRequest() {
        return AuthRequest.builder()
            .usernameOrEmail(USERNAME)
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

    private AuthenticatedUser createAuthenticatedUser() {
        return AuthenticatedUser.builder()
            .id(USER_ID)
            .authorities(AUTHORITIES)
            .build();
    }

    private UserAuthDetails createUserAuthDetails() {
        return UserAuthDetails.builder()
            .id(USER_ID)
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .firstName(FIRST_NAME)
            .authorities(AUTHORITIES)
            .build();
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);
    }
}
