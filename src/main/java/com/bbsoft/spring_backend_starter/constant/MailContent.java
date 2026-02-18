package com.bbsoft.spring_backend_starter.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MailContent {

    LOGO("logo", "templates/contents/logo.png");

    private final String contentId;
    private final String pathFromResources;
}
