package com.bbsoft.spring_backend_starter.controller.validation.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class UsernameValidatorTest {

    private final UsernameValidator validator = new UsernameValidator();

    @Test
    public void isValid_ShouldBeTrueWhenValueIsNull() {
        Assertions.assertTrue(validator.isValid(null, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeFalseWhenUsernameShort() {
        // GIVEN
        var username = "abc";
        // THEN
        Assertions.assertFalse(validator.isValid(username, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeFalseWhenUsernameContainsInvalidCharacter() {
        // GIVEN
        var username = "user!";
        // THEN
        Assertions.assertFalse(validator.isValid(username, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeFalseWhenUsernameStartsWithNonLetterCharacter() {
        // GIVEN
        var username = "_user";
        // THEN
        Assertions.assertFalse(validator.isValid(username, Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void isValid_ShouldBeTrueWhenUsernameValid() {
        // GIVEN
        var username = "user_";
        // THEN
        Assertions.assertTrue(validator.isValid(username, Mockito.mock(ConstraintValidatorContext.class)));
    }
}
