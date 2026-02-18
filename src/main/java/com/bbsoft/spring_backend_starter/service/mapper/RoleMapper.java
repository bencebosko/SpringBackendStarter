package com.bbsoft.spring_backend_starter.service.mapper;

import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.repository.entity.UserRole;
import org.mapstruct.Mapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Objects;

@Mapper(componentModel = "spring")
public class RoleMapper {

    public static final String ROLE_PREFIX = "ROLE_";

    public UserRole toUserRole(Role role) {
        if (Objects.isNull(role)) {
            return null;
        }
        return UserRole.builder().role(role).build();
    }

    public Role toRole(UserRole userRole) {
        if (Objects.isNull(userRole)) {
            return null;
        }
        return userRole.getRole();
    }

    public GrantedAuthority toAuthority(Role role) {
        if (Objects.isNull(role)) {
            return null;
        }
        return new SimpleGrantedAuthority(ROLE_PREFIX + role.getName());
    }

    public GrantedAuthority toAuthority(UserRole userRole) {
        if (Objects.isNull(userRole)) {
            return null;
        }
        return toAuthority(userRole.getRole());
    }
}
