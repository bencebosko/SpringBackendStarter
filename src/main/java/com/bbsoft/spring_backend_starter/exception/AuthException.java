package com.bbsoft.spring_backend_starter.exception;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import org.springframework.http.HttpStatus;

public class AuthException extends SpringBackendException {

    private AuthException(String errorCode, String message) {
        super(HttpStatus.UNAUTHORIZED, errorCode, message);
    }

    public static AuthException createAuthenticationRequired(String message) {
        return new AuthException(ErrorCodes.AUTHENTICATION_REQUIRED, message);
    }
}
