package com.bbsoft.spring_backend_starter.exception.dto;

import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Map;

@Getter
public class ValidationErrorResponse extends ErrorResponse {

    private static final String MESSAGE = "Request is invalid.";
    private final Map<String, String> validationErrors;

    public ValidationErrorResponse(String errorCode, ZonedDateTime occurredAt, Map<String, String> validationErrors) {
        super(errorCode, MESSAGE, occurredAt);
        this.validationErrors = validationErrors;
    }
}
