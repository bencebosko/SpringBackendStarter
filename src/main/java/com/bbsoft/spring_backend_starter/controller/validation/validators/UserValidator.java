package com.bbsoft.spring_backend_starter.controller.validation.validators;

import com.bbsoft.spring_backend_starter.exception.ValidationException;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Transactional
public class UserValidator {

    private final UserRepository userRepository;

    public void validateEmailAndUsernameNotExist(String email, String username) {
        validateEmailNotExist(email);
        validateUsernameNotExist(username);
    }

    private void validateEmailNotExist(String email) {
        if (Objects.nonNull(email) && userRepository.findByEmail(email).isPresent()) {
            throw ValidationException.createEmailAlreadyExists(email);
        }
    }

    private void validateUsernameNotExist(String username) {
        if (Objects.nonNull(username) && userRepository.findByUsername(username).isPresent()) {
            throw ValidationException.createUsernameAlreadyExists(username);
        }
    }
}
