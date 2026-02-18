package com.bbsoft.spring_backend_starter.exception;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import org.springframework.http.HttpStatus;

public class UserVerificationException extends SpringBackendException {

    private UserVerificationException(String errorCode, String message) {
        super(HttpStatus.UNAUTHORIZED, errorCode, message);
    }

    public static UserVerificationException createInvalidVerificationCode(int verificationCode) {
        return new UserVerificationException(ErrorCodes.INVALID_VERIFICATION_CODE, "Invalid verification code: " + verificationCode);
    }

    public static UserVerificationException createInvalidRefreshToken(String refreshToken) {
        return new UserVerificationException(ErrorCodes.INVALID_REFRESH_TOKEN, "Invalid refresh token: " + refreshToken);
    }

    public static UserVerificationException createInvalidConfirmationPassword() {
        return new UserVerificationException(ErrorCodes.INVALID_CONFIRMATION_PASSWORD, "Invalid confirmation password.");
    }
}
