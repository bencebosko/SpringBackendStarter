package com.bbsoft.spring_backend_starter.constant;

public final class ErrorCodes {

    private ErrorCodes() {}

    // TooManyRequests
    public static final String RATE_LIMIT_EXCEEDED = "rate-limit-exceeded";
    // Unauthorized
    public static final String AUTHENTICATION_REQUIRED = "authentication-required";
    public static final String INVALID_USERNAME_OR_PASSWORD = "invalid-username-or-password";
    public static final String INVALID_JWT = "invalid-jwt";
    public static final String INVALID_VERIFICATION_CODE = "invalid-verification-code";
    public static final String INVALID_CONFIRMATION_PASSWORD = "invalid-confirmation-password";
    public static final String INVALID_REFRESH_TOKEN = "invalid-refresh-token";
    // Forbidden
    public static final String ACCESS_DENIED = "access-denied";
    // NotFound
    public static final String ENTITY_NOT_FOUND = "entity-not-found";
    // BadRequest
    public static final String INVALID_REQUEST = "invalid-request";
    public static final String EMAIL_ALREADY_EXISTS = "email-already-exists";
    public static final String USERNAME_ALREADY_EXISTS = "username-already-exists";
    // Conflict
    public static final String OPTIMISTIC_LOCKING = "optimistic-locking";
    // InternalServerError
    public static final String SPRING_BACKEND_ERROR = "spring-backend-error";
    public static final String MAIL_SENDING_ERROR = "mail-sending-error";
}
