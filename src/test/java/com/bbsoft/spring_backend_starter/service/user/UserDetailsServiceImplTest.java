package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.entity.User;
import com.bbsoft.spring_backend_starter.service.mapper.UserMapper;
import com.bbsoft.spring_backend_starter.service.mapper.UserMapperImpl;
import com.bbsoft.spring_backend_starter.service.mapper.RoleMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @Mock
    private UserRepository userRepository;
    @Spy
    private final UserMapper userMapper = new UserMapperImpl(new RoleMapperImpl());
    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Test
    public void loadUserByUsername_ShouldThrowExceptionIfUsernameAndEmailNotExists() {
        // GIVEN
        var notExistingUsernameAndEmail = "notExistingUsernameAndEmail";
        Mockito.when(userRepository.findByUsername(notExistingUsernameAndEmail)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(notExistingUsernameAndEmail)).thenReturn(Optional.empty());
        // THEN
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userDetailsServiceImpl.loadUserByUsername(notExistingUsernameAndEmail));
    }

    @Test
    public void loadUserByUsername_ShouldLoadTheUserByUsername() {
        // GIVEN
        var savedUser = createUser();
        Mockito.when(userRepository.findByUsername(savedUser.getUsername())).thenReturn(Optional.of(savedUser));
        // WHEN
        var userDetails = userDetailsServiceImpl.loadUserByUsername(savedUser.getUsername());
        // THEN
        Assertions.assertEquals(savedUser.getId(), userDetails.getId());
        Assertions.assertEquals(savedUser.getUsername(), userDetails.getUsername());
        Assertions.assertEquals(savedUser.getEmail(), userDetails.getEmail());
        Assertions.assertEquals(savedUser.getEncodedPassword(), userDetails.getPassword());
    }

    @Test
    public void loadUserByUsername_ShouldLoadTheUserByEmail() {
        // GIVEN
        var savedUser = createUser();
        Mockito.when(userRepository.findByUsername(savedUser.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(savedUser.getEmail())).thenReturn(Optional.of(savedUser));
        // WHEN
        var userDetails = userDetailsServiceImpl.loadUserByUsername(savedUser.getEmail());
        // THEN
        Assertions.assertEquals(savedUser.getId(), userDetails.getId());
        Assertions.assertEquals(savedUser.getUsername(), userDetails.getUsername());
        Assertions.assertEquals(savedUser.getEmail(), userDetails.getEmail());
        Assertions.assertEquals(savedUser.getEncodedPassword(), userDetails.getPassword());
    }

    private User createUser() {
        return User.builder()
            .username(USERNAME)
            .email(EMAIL)
            .encodedPassword(ENCODED_PASSWORD)
            .build();
    }
}
