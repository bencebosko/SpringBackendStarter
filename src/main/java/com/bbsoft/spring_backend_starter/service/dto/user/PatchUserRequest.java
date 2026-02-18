package com.bbsoft.spring_backend_starter.service.dto.user;

import jakarta.validation.Valid;
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
public class PatchUserRequest {

    @Valid
    private UserPatch userPatch;
    @NotNull
    private String username;
    @NotNull
    private String confirmationPassword;
}
