package com.bbsoft.spring_backend_starter.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MailType {

    LOGIN_VERIFICATION("login-verification");

    private final String templateName;
}
