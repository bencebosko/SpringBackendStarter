package com.bbsoft.spring_backend_starter.exception;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import org.springframework.http.HttpStatus;

public class ValidationException extends SpringBackendException {

    private ValidationException(String errorCode, String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

    public static ValidationException createUsernameAlreadyExists(String username) {
        return new ValidationException(ErrorCodes.USERNAME_ALREADY_EXISTS, "Username already exists: " + username);
    }

    public static ValidationException createEmailAlreadyExists(String email) {
        return new ValidationException(ErrorCodes.EMAIL_ALREADY_EXISTS, "Email already exists: " + email);
    }
}
