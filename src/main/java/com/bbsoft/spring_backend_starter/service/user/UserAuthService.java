package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.exception.EntityNotFoundException;
import com.bbsoft.spring_backend_starter.exception.UserVerificationException;
import com.bbsoft.spring_backend_starter.repository.UserAuthRepository;
import com.bbsoft.spring_backend_starter.repository.entity.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;

    public void setVerificationCode(Long userId, int verificationCode) {
        userAuthRepository.findByUserId(userId).ifPresentOrElse(userAuth -> {
            userAuth.setVerificationCode(verificationCode);
            userAuthRepository.save(userAuth);
        }, () -> userAuthRepository.save(UserAuth.builder()
            .userId(userId)
            .verificationCode(verificationCode)
            .build()));
    }

    public void verifyUser(Long userId, int verificationCode) {
        userAuthRepository.findByUserId(userId).ifPresentOrElse(userAuth -> {
            if (Objects.equals(userAuth.getVerificationCode(), verificationCode)) {
                userAuth.setVerificationCode(null);
                userAuthRepository.save(userAuth);
                log.info("User verified with id: {}", userId);
            } else {
                throw UserVerificationException.createInvalidVerificationCode(verificationCode);
            }
        }, () -> {
            throw EntityNotFoundException.createUserAuthNotFound(userId);
        });
    }

    public void setRefreshToken(Long userId, String refreshToken) {
        userAuthRepository.findByUserId(userId).ifPresentOrElse(userAuth -> {
            userAuth.setRefreshToken(refreshToken);
            userAuthRepository.save(userAuth);
        }, () -> userAuthRepository.save(UserAuth.builder()
            .userId(userId)
            .refreshToken(refreshToken)
            .build()));
    }

    @Transactional(readOnly = true)
    public void verifyRefreshToken(Long userId, String refreshToken) {
        userAuthRepository.findByUserId(userId).ifPresentOrElse(userAuth -> {
            if (!Objects.equals(userAuth.getRefreshToken(), refreshToken)) {
                throw UserVerificationException.createInvalidRefreshToken(refreshToken);
            }
            log.info("Refresh token verified for user: {}", userId);
        }, () -> {
            throw EntityNotFoundException.createUserAuthNotFound(userId);
        });
    }
}
