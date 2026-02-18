package com.bbsoft.spring_backend_starter.service.dto.user;

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
public class DeleteUserRequest {

    @NotNull
    private String username;
    @NotNull
    private String confirmationPassword;
}
