package com.bbsoft.spring_backend_starter.controller.validation.validators;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import com.bbsoft.spring_backend_starter.exception.ValidationException;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    private final static String USERNAME = "username";
    private final static String EMAIL = "email";

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserValidator userValidator;

    @Test
    public void validateEmailAndUsernameNotExist_ShouldNotThrowExceptionIfEmailAndUsernameIsNull() {
        Assertions.assertDoesNotThrow(() -> userValidator.validateEmailAndUsernameNotExist(null, null));
    }

    @Test
    public void validateEmailAndUsernameNotExist_ShouldThrowExceptionIfEmailExist() {
        // GIVEN
        var savedUser = User.builder().build();
        var notExistingUsername = "notExistingUsername";
        Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedUser));
        // THEN
        var thrownException = Assertions.assertThrows(ValidationException.class, () -> userValidator.validateEmailAndUsernameNotExist(EMAIL, notExistingUsername));
        Assertions.assertEquals(ErrorCodes.EMAIL_ALREADY_EXISTS, thrownException.getErrorCode());
    }

    @Test
    public void validateEmailAndUsernameNotExist_ShouldThrowExceptionIfUsernameExist() {
        // GIVEN
        var savedUser = User.builder().build();
        var notExistingEmail = "notExistingEmail";
        Mockito.when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(savedUser));
        // THEN
        var thrownException = Assertions.assertThrows(ValidationException.class, () -> userValidator.validateEmailAndUsernameNotExist(notExistingEmail, USERNAME));
        Assertions.assertEquals(ErrorCodes.USERNAME_ALREADY_EXISTS, thrownException.getErrorCode());
    }
}
