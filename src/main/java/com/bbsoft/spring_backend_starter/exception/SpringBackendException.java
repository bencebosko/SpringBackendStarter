package com.bbsoft.spring_backend_starter.exception;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SpringBackendException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    public SpringBackendException(String message) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.SPRING_BACKEND_ERROR, message);
    }

    protected SpringBackendException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
