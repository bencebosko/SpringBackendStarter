package com.bbsoft.spring_backend_starter.service.dto;

import jakarta.validation.constraints.NotNull;
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
public class LoginRequest {

    @NotNull
    private String usernameOrEmail;
    @NotNull
    private String password;
    @NotNull
    private Integer verificationCode;
}
