package com.bbsoft.spring_backend_starter.controller.validation.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class StrongPasswordValidatorTest {

    private final StrongPasswordValidator strongPasswordValidator = new StrongPasswordValidator();

    @Test
    public void isValid_ShouldBeTrueWhenValueIsNull() {
        Assertions.assertTrue(strongPasswordValidator.isValid(null, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeFalseWhenPasswordContainsInvalidCharacter() {
        // GIVEN
        var password = "Jelszó123 ";
        // THEN
        Assertions.assertFalse(strongPasswordValidator.isValid(password, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeFalseWhenPasswordNotContainUppercaseLetter() {
        // GIVEN
        var password = "jelszó123!";
        // THEN
        Assertions.assertFalse(strongPasswordValidator.isValid(password, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeFalseWhenPasswordNotContainLowercaseLetter() {
        // GIVEN
        var password = "JELSZÓ123!";
        // THEN
        Assertions.assertFalse(strongPasswordValidator.isValid(password, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeFalseWhenPasswordNotContainDigit() {
        // GIVEN
        var password = "Jelszószám!";
        // THEN
        Assertions.assertFalse(strongPasswordValidator.isValid(password, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeFalseWhenPasswordNotContainSpecialCharacter() {
        // GIVEN
        var password = "Jelszó123";
        // THEN
        Assertions.assertFalse(strongPasswordValidator.isValid(password, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeFalseWhenPasswordShort() {
        // GIVEN
        var password = "Jelszó1";
        // THEN
        Assertions.assertFalse(strongPasswordValidator.isValid(password, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeTrueWhenPasswordValid() {
        // GIVEN
        var password = "Jelszó123!";
        // THEN
        Assertions.assertTrue(strongPasswordValidator.isValid(password, Mockito.mock(ConstraintValidatorContext.class)));
    }
}
