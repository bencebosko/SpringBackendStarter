package com.bbsoft.spring_backend_starter.controller.validation.validators;

import com.bbsoft.spring_backend_starter.controller.validation.Username;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Objects.isNull(value) || value.matches("[a-z][a-z0-9_.]{4,}");
    }
}
