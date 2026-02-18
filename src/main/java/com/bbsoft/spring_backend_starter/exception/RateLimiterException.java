package com.bbsoft.spring_backend_starter.exception;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import org.springframework.http.HttpStatus;

public class RateLimiterException extends SpringBackendException {

    public RateLimiterException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, ErrorCodes.RATE_LIMIT_EXCEEDED, message);
    }
}
