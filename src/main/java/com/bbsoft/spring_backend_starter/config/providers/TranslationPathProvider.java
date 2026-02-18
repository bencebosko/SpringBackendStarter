package com.bbsoft.spring_backend_starter.config.providers;

import org.springframework.stereotype.Component;

@Component
public class TranslationPathProvider {

    public String getTranslationPathMatcher() {
        return "classpath:translations/*.json";
    }
}
