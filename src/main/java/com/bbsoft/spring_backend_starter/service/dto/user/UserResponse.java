package com.bbsoft.spring_backend_starter.service.dto.user;

import com.bbsoft.spring_backend_starter.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<Role> roles;
}
