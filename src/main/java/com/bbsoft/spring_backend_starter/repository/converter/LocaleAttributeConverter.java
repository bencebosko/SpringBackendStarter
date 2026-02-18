package com.bbsoft.spring_backend_starter.repository.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;
import java.util.Objects;

@Converter
public class LocaleAttributeConverter implements AttributeConverter<Locale, String> {

    @Override
    public String convertToDatabaseColumn(Locale locale) {
        if (Objects.isNull(locale)) {
            return null;
        }
        return locale.toLanguageTag();
    }

    @Override
    public Locale convertToEntityAttribute(String dbValue) {
        if (Objects.isNull(dbValue)) {
            return null;
        }
        return Locale.forLanguageTag(dbValue);
    }
}
