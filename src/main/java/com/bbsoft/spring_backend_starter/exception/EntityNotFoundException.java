package com.bbsoft.spring_backend_starter.exception;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import com.bbsoft.spring_backend_starter.constant.Role;
import org.springframework.http.HttpStatus;

import java.util.Set;

public class EntityNotFoundException extends SpringBackendException {

    private EntityNotFoundException(String errorCode, String message) {
        super(HttpStatus.NOT_FOUND, errorCode, message);
    }

    public static EntityNotFoundException createUserNotFound(Long userId) {
        return new EntityNotFoundException(ErrorCodes.ENTITY_NOT_FOUND, "User not found with id: " + userId);
    }

    public static EntityNotFoundException createUserNotFound(String username) {
        return new EntityNotFoundException(ErrorCodes.ENTITY_NOT_FOUND, "User not found with username: " + username);
    }

    public static EntityNotFoundException createUserAuthNotFound(Long userId) {
        return new EntityNotFoundException(ErrorCodes.ENTITY_NOT_FOUND, "UserAuth not found for user: " + userId);
    }

    public static EntityNotFoundException createUserRoleNotFound(Set<Role> notFoundRoles) {
        return new EntityNotFoundException(ErrorCodes.ENTITY_NOT_FOUND, "UserRole not found: " + notFoundRoles);
    }

    public static EntityNotFoundException createUserSettingsNotFound(Long userId) {
        return new EntityNotFoundException(ErrorCodes.ENTITY_NOT_FOUND, "UserSettings not found for user: " + userId);
    }
}
