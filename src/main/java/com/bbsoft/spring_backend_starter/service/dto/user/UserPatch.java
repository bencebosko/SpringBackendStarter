package com.bbsoft.spring_backend_starter.service.dto.user;

import com.bbsoft.spring_backend_starter.controller.validation.StrongPassword;
import com.bbsoft.spring_backend_starter.controller.validation.Username;
import jakarta.validation.constraints.Email;
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
public class UserPatch {

    @Username
    private String username;
    @Email
    private String email;
    @StrongPassword
    private String password;
    @Pattern(regexp = "\\p{L}{3,}")
    private String firstName;
    @Pattern(regexp = "\\p{L}{3,}")
    private String lastName;
}
