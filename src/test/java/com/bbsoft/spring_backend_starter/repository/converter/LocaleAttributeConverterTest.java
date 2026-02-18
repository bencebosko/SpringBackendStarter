package com.bbsoft.spring_backend_starter.repository.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class LocaleAttributeConverterTest {

    private static final Locale LOCALE = Locale.US;
    private static final String LOCALE_VALUE = "en-US";

    private final LocaleAttributeConverter converter = new LocaleAttributeConverter();

    @Test
    public void convertToDatabaseColumn_ShouldConvertNullToNull() {
        Assertions.assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    public void convertToDatabaseColumn_ShouldConvertLocaleToString() {
        Assertions.assertEquals(LOCALE_VALUE, converter.convertToDatabaseColumn(LOCALE));
    }

    @Test
    public void convertToEntityAttribute_ShouldConvertNullToNull() {
        Assertions.assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    public void convertToEntityAttribute_ShouldConvertStringToLocale() {
        Assertions.assertEquals(LOCALE, converter.convertToEntityAttribute(LOCALE_VALUE));
    }
}
