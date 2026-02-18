package com.bbsoft.spring_backend_starter.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Getter
public class ErrorResponse {

    private final String errorCode;
    private final String message;
    private final ZonedDateTime occurredAt;
}
