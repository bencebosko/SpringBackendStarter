package com.bbsoft.spring_backend_starter.service;

import com.bbsoft.spring_backend_starter.config.ObjectMapperProvider;
import com.bbsoft.spring_backend_starter.config.providers.TranslationPathProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

@ExtendWith(MockitoExtension.class)
public class TranslationServiceTest {

    private static final String SIMPLE_TRANSLATION_KEY = "test.key.simple-translation";
    private static final String SIMPLE_TRANSLATION = "Simple";
    private static final String KEY_NOT_FOUND = "test.key.not-found";
    private static final String TRANSLATION_WITH_REPLACEMENTS_KEY = "test.key.translation-with-replacements";
    private static final String TRANSLATION_WITH_REPLACEMENTS = "Translation{{first}}{{second}}";

    private static final Locale LOCALE_GB = Locale.of("en", "GB");
    private static final Locale LOCALE_US = Locale.of("en", "US");
    private static final Locale LOCALE_DE = Locale.of("de", "DE");

    private final ObjectMapperProvider objectMapperProvider = new ObjectMapperProvider();
    @Spy
    private final ObjectMapper objectMapper = objectMapperProvider.getObjectMapper();
    @Mock
    private TranslationPathProvider translationPathProvider;
    @InjectMocks
    private TranslationService translationService;

    @BeforeEach
    public void initMocks() {
        var testTranslationPath = "classpath:test/*.json";
        Mockito.when(translationPathProvider.getTranslationPathMatcher()).thenReturn(testTranslationPath);
        translationService.loadTranslations();
    }

    @Test
    public void translate_ShouldReturnNullIfLocaleNotFound() {
        Assertions.assertNull(translationService.translate(SIMPLE_TRANSLATION_KEY, LOCALE_DE));
    }

    @Test
    public void translate_ShouldReturnNullIfTranslationKeyNotFound() {
        Assertions.assertNull(translationService.translate(KEY_NOT_FOUND, LOCALE_GB));
    }

    @Test
    public void translate_ShouldReturnNullIfTranslationKeyNotFoundWithReplacements() {
        Assertions.assertNull(translationService.translate(KEY_NOT_FOUND, LOCALE_GB, new Object[]{null}));
    }

    @Test
    public void translate_ShouldReturnTheTranslationForEachLocale() {
        Assertions.assertEquals(SIMPLE_TRANSLATION, translationService.translate(SIMPLE_TRANSLATION_KEY, LOCALE_GB));
        Assertions.assertEquals(SIMPLE_TRANSLATION, translationService.translate(SIMPLE_TRANSLATION_KEY, LOCALE_US));
    }

    @Test
    public void translate_ShouldReturnTheTranslationWithReplacement() {
        // GIVEN
        var firstReplacement = "first";
        var secondReplacement = "second";
        var expectedTranslation = "Translationfirstsecond";
        // WHEN
        var replacedTranslation = translationService.translate(TRANSLATION_WITH_REPLACEMENTS_KEY, LOCALE_GB, firstReplacement, secondReplacement);
        // THEN
        Assertions.assertEquals(expectedTranslation, replacedTranslation);
    }

    @Test
    public void translate_ShouldReturnTranslationWithoutReplacement() {
        // WHEN
        var replacedTranslation = translationService.translate(TRANSLATION_WITH_REPLACEMENTS_KEY, LOCALE_GB);
        // THEN
        Assertions.assertEquals(TRANSLATION_WITH_REPLACEMENTS, replacedTranslation);
    }

    @Test
    public void translate_ShouldReplaceWithEmptyStringIfReplacementIsNull() {
        // GIVEN
        var expectedTranslation = "Translation";
        // WHEN
        var replacedTranslation = translationService.translate(TRANSLATION_WITH_REPLACEMENTS_KEY, LOCALE_GB, null, null);
        // THEN
        Assertions.assertEquals(expectedTranslation, replacedTranslation);
    }
}
