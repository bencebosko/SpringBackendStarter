package com.bbsoft.spring_backend_starter.controller;

import com.bbsoft.spring_backend_starter.security.SecurityHelper;
import com.bbsoft.spring_backend_starter.service.dto.user.CreateUserRequest;
import com.bbsoft.spring_backend_starter.service.dto.user.DeleteUserRequest;
import com.bbsoft.spring_backend_starter.service.dto.user.PatchUserRequest;
import com.bbsoft.spring_backend_starter.service.dto.user.UserResponse;
import com.bbsoft.spring_backend_starter.service.user.UserService;
import com.bbsoft.spring_backend_starter.controller.validation.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final SecurityHelper securityHelper;

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getOwn() {
        var authenticatedUser = securityHelper.getAuthenticatedUser();
        log.info("Getting own user with id: {}", authenticatedUser.getId());
        return ResponseEntity.ok(userService.findUserById(authenticatedUser.getId()));
    }

    @PostMapping("/admin/users")
    public ResponseEntity<Void> create(@Validated @RequestBody CreateUserRequest createUserRequest) {
        final var userRequest = createUserRequest.getUserRequest();
        log.info("Creating user with username: {}", userRequest.getUsername());
        userService.confirmPassword(securityHelper.getAuthenticatedUser().getId(), createUserRequest.getConfirmationPassword());
        userValidator.validateEmailAndUsernameNotExist(userRequest.getEmail(), userRequest.getUsername());
        userService.createSimpleUser(userRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/users")
    public ResponseEntity<Void> patch(@Validated @RequestBody PatchUserRequest patchUserRequest) {
        log.info("Patching user with username: {}", patchUserRequest.getUsername());
        userService.confirmPassword(securityHelper.getAuthenticatedUser().getId(), patchUserRequest.getConfirmationPassword());
        var userPatch = patchUserRequest.getUserPatch();
        userValidator.validateEmailAndUsernameNotExist(userPatch.getEmail(), userPatch.getUsername());
        userService.patchUser(patchUserRequest.getUsername(), userPatch);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/users")
    public ResponseEntity<Void> delete(@Validated @RequestBody DeleteUserRequest deleteUserRequest) {
        log.info("Deleting user with username: {}", deleteUserRequest.getUsername());
        userService.confirmPassword(securityHelper.getAuthenticatedUser().getId(), deleteUserRequest.getConfirmationPassword());
        userService.deleteUserByUsername(deleteUserRequest.getUsername());
        return ResponseEntity.ok().build();
    }
}
