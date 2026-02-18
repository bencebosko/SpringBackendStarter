package com.bbsoft.spring_backend_starter.service;

import com.bbsoft.spring_backend_starter.config.providers.TranslationPathProvider;
import com.bbsoft.spring_backend_starter.exception.SpringBackendException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    private static final String TRANSLATION_FILE_NAME = "^[a-zA-Z]{2}-[a-zA-Z]{2}.json$";
    private static final String TRANSLATION_PARAM = "\\{\\{\\s*[a-zA-Z]+\\s*}}";

    private final ObjectMapper objectMapper;
    private final TranslationPathProvider translationPathProvider;
    private final Map<Locale, Map<String, String>> loadedTranslations = new HashMap<>();

    public void loadTranslations() {
        loadedTranslations.clear();
        try {
            var pathMatchingResolver = new PathMatchingResourcePatternResolver();
            final var resources = pathMatchingResolver.getResources(translationPathProvider.getTranslationPathMatcher());
            for (Resource resource : resources) {
                if (Objects.nonNull(resource.getFilename()) && resource.getFilename().matches(TRANSLATION_FILE_NAME)) {
                    var locale = Locale.forLanguageTag(resource.getFilename().split("\\.")[0]);
                    loadedTranslations.put(locale, readTranslation(resource));
                }
            }
        } catch (IOException exception) {
            throw new SpringBackendException("Translations cannot be loaded: " + exception.getMessage());
        }
        log.info("Translations loaded successfully. {}", loadedTranslations.keySet());
    }

    public String translate(String key, Locale locale) {
        final var translationsOfLocale = loadedTranslations.get(locale);
        if (Objects.nonNull(translationsOfLocale)) {
            return translationsOfLocale.get(key);
        }
        return null;
    }

    public String translate(String key, Locale locale, Object... replacementValues) {
        final var translation = translate(key, locale);
        if (Objects.nonNull(translation)) {
            String translationToReplace = translation;
            for (Object value : replacementValues) {
                final var replacement = Objects.nonNull(value) ? value.toString() : "";
                translationToReplace = translationToReplace.replaceFirst(TRANSLATION_PARAM, replacement);
            }
            return translationToReplace;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> readTranslation(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return objectMapper.readValue(json, Map.class);
        }
    }
}
