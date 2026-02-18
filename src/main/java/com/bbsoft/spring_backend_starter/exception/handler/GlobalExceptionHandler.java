package com.bbsoft.spring_backend_starter.exception.handler;

import com.bbsoft.spring_backend_starter.config.providers.ClockProvider;
import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import com.bbsoft.spring_backend_starter.exception.SpringBackendException;
import com.bbsoft.spring_backend_starter.exception.dto.ErrorResponse;
import com.bbsoft.spring_backend_starter.exception.dto.ValidationErrorResponse;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ClockProvider clockProvider;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException exception,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        log.info(getStackTraceAsString(exception));
        var errorResponse = createValidationErrorResponse(exception.getBindingResult());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({AuthenticationException.class})
    protected ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException exception) {
        log.info(getStackTraceAsString(exception));
        ErrorResponse errorResponse;
        if (exception instanceof BadCredentialsException) {
            errorResponse = createErrorResponse(ErrorCodes.INVALID_USERNAME_OR_PASSWORD, exception.getMessage());
        } else {
            errorResponse = createErrorResponse(ErrorCodes.AUTHENTICATION_REQUIRED, exception.getMessage());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler({JwtException.class})
    protected ResponseEntity<ErrorResponse> handleJwt(JwtException ex) {
        log.info(getStackTraceAsString(ex));
        var errorResponse = createErrorResponse(ErrorCodes.INVALID_JWT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler({AccessDeniedException.class})
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
        log.info(getStackTraceAsString(exception));
        var errorResponse = createErrorResponse(ErrorCodes.ACCESS_DENIED, exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler({OptimisticLockingFailureException.class})
    protected ResponseEntity<ErrorResponse> handleOptimisticLockingFailureException(OptimisticLockingFailureException exception) {
        log.info(getStackTraceAsString(exception));
        var errorResponse = createErrorResponse(ErrorCodes.OPTIMISTIC_LOCKING, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler({SpringBackendException.class})
    protected ResponseEntity<ErrorResponse> handleSpringBackendException(SpringBackendException exception) {
        log.info(getStackTraceAsString(exception));
        var errorResponse = createErrorResponse(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error(getStackTraceAsString(exception));
        var errorResponse = createErrorResponse(ErrorCodes.SPRING_BACKEND_ERROR, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private String getStackTraceAsString(Exception exception) {
        final var stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    private ErrorResponse createErrorResponse(String errorCode, String message) {
        return new ErrorResponse(errorCode, message, ZonedDateTime.now(clockProvider.getClock()));
    }

    private ValidationErrorResponse createValidationErrorResponse(BindingResult bindingResult) {
        final Map<String, String> errorsByField = new HashMap<>();
        bindingResult.getAllErrors().forEach((error) -> {
            errorsByField.put(((FieldError) error).getField(), error.getDefaultMessage());
        });
        return new ValidationErrorResponse(ErrorCodes.INVALID_REQUEST, ZonedDateTime.now(clockProvider.getClock()), errorsByField);
    }
}
