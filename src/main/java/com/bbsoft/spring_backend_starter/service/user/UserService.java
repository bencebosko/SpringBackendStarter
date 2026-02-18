package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.exception.EntityNotFoundException;
import com.bbsoft.spring_backend_starter.exception.UserVerificationException;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.entity.User;
import com.bbsoft.spring_backend_starter.service.dto.user.UserDTO;
import com.bbsoft.spring_backend_starter.service.dto.user.UserPatch;
import com.bbsoft.spring_backend_starter.service.dto.user.UserRequest;
import com.bbsoft.spring_backend_starter.service.dto.user.UserResponse;
import com.bbsoft.spring_backend_starter.service.helper.ObjectMerger;
import com.bbsoft.spring_backend_starter.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserSettingsService userSettingsService;
    private final UserRoleService userRoleService;
    private final UserMapper userMapper;
    private final ObjectMerger objectMerger;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createSimpleUser(UserRequest userRequest) {
        return createUserWithRoles(userRequest, Set.of(Role.AUTHENTICATED_USER, Role.SIMPLE_USER));
    }

    public UserDTO createUserWithRoles(UserRequest userRequest, Set<Role> roles) {
        var userRoles = userRoleService.collectUserRoles(roles);
        var user = userMapper.toUser(userRequest, userRoles);
        final var savedUser = userRepository.save(user);
        userSettingsService.createDefaultSettings(savedUser.getId());
        log.info("User created with username: {} and roles: {}", savedUser.getUsername(), roles);
        return userMapper.toUserDTO(savedUser);
    }

    public UserDTO patchUser(String username, UserPatch userPatch) {
        return userRepository.findByUsername(username)
            .map(user -> mergeUser(user, userPatch))
            .map(mergedUser -> {
                var updatedUser = userRepository.save(mergedUser);
                log.info("User patched with username: {}", username);
                return updatedUser;
            })
            .map(userMapper::toUserDTO)
            .orElseThrow(() -> EntityNotFoundException.createUserNotFound(username));
    }

    public void deleteUserByUsername(String username) {
        userRepository.deleteByUsername(username);
        log.info("User deleted with username: {}", username);
    }

    @Transactional(readOnly = true)
    public void confirmPassword(Long userId, String password) {
        userRepository.findUserPasswordById(userId).ifPresentOrElse(userPassword -> {
            if (!passwordEncoder.matches(password, userPassword.getEncodedPassword())) {
                throw UserVerificationException.createInvalidConfirmationPassword();
            }
        }, () -> {
            throw EntityNotFoundException.createUserNotFound(userId);
        });
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long userId) {
        return userRepository.findById(userId).map(userMapper::toUserResponse).orElseThrow(() -> EntityNotFoundException.createUserNotFound(userId));
    }

    private User mergeUser(User user, UserPatch userPatch) {
        var patchedUser = userMapper.toUser(userPatch);
        return objectMerger.shallowMerge(user, patchedUser, User.class, false);
    }
}
