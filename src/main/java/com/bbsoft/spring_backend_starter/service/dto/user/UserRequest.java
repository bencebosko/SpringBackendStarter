package com.bbsoft.spring_backend_starter.service.dto.user;

import com.bbsoft.spring_backend_starter.controller.validation.StrongPassword;
import com.bbsoft.spring_backend_starter.controller.validation.Username;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    @NotNull
    @Username
    private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    @StrongPassword
    private String password;
    @NotNull
    @Pattern(regexp = "\\p{L}{3,}")
    private String firstName;
    @NotNull
    @Pattern(regexp = "\\p{L}{3,}")
    private String lastName;
}
