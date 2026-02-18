package com.bbsoft.spring_backend_starter.service;

import com.bbsoft.spring_backend_starter.config.properties.SpringBackendProperties;
import com.bbsoft.spring_backend_starter.security.JwtTokenHelper;
import com.bbsoft.spring_backend_starter.service.dto.AuthRequest;
import com.bbsoft.spring_backend_starter.service.dto.AuthResponse;
import com.bbsoft.spring_backend_starter.service.dto.AuthenticatedUser;
import com.bbsoft.spring_backend_starter.service.dto.LoginRequest;
import com.bbsoft.spring_backend_starter.service.dto.TokenDTO;
import com.bbsoft.spring_backend_starter.service.dto.UserAuthDetails;
import com.bbsoft.spring_backend_starter.service.helper.VerificationCodeGenerator;
import com.bbsoft.spring_backend_starter.service.mail.SendMailFactory;
import com.bbsoft.spring_backend_starter.service.mail.MailService;
import com.bbsoft.spring_backend_starter.service.user.UserAuthService;
import com.bbsoft.spring_backend_starter.service.user.UserSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserAuthService userAuthService;
    private final UserSettingsService userSettingsService;
    private final MailService mailService;
    private final SendMailFactory sendMailFactory;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final JwtTokenHelper jwtTokenHelper;
    private final SpringBackendProperties springBackendProperties;

    public AuthResponse authenticate(AuthRequest authRequest) {
        if (!springBackendProperties.getLoginVerificationEnabled()) {
            return AuthResponse.builder().loginVerificationEnabled(false).build();
        }
        final var authenticatedUser = doAuthenticate(authRequest.getUsernameOrEmail(), authRequest.getPassword());
        final var verificationCode = verificationCodeGenerator.generate();
        userAuthService.setVerificationCode(authenticatedUser.getId(), verificationCode);
        var loginVerificationMail = sendMailFactory.createLoginVerificationMail(authenticatedUser, verificationCode, userSettingsService.findLocaleByUserId(authenticatedUser.getId()));
        mailService.saveMail(loginVerificationMail);
        return AuthResponse.builder().loginVerificationEnabled(true).build();
    }

    @SuppressWarnings("unchecked")
    public TokenDTO login(LoginRequest loginRequest) {
        final var authenticatedUser = doAuthenticate(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        if (springBackendProperties.getLoginVerificationEnabled()) {
            userAuthService.verifyUser(authenticatedUser.getId(), loginRequest.getVerificationCode());
        }
        final var refreshToken = jwtTokenHelper.generateRefreshToken(authenticatedUser.getId(), (List<GrantedAuthority>) authenticatedUser.getAuthorities());
        final var accessToken = jwtTokenHelper.generateAccessToken(authenticatedUser.getId(), (List<GrantedAuthority>) authenticatedUser.getAuthorities());
        userAuthService.setRefreshToken(authenticatedUser.getId(), refreshToken);
        log.info("Access token successfully generated for user: {}", loginRequest.getUsernameOrEmail());
        return TokenDTO.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public void logout(Long userId) {
        userAuthService.setRefreshToken(userId, null);
    }

    public String refreshAccessToken(AuthenticatedUser authenticatedUser, String refreshToken) {
        userAuthService.verifyRefreshToken(authenticatedUser.getId(), refreshToken);
        final var accessToken = jwtTokenHelper.generateAccessToken(authenticatedUser.getId(), authenticatedUser.getAuthorities());
        log.info("Access token refreshed for user with id: {}", authenticatedUser.getId());
        return accessToken;
    }

    private UserAuthDetails doAuthenticate(String usernameOrEmail, String password) {
        return (UserAuthDetails) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password)).getPrincipal();
    }
}
