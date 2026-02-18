package com.bbsoft.spring_backend_starter.service.mapper;

import com.bbsoft.spring_backend_starter.repository.entity.User;
import com.bbsoft.spring_backend_starter.repository.entity.UserRole;
import com.bbsoft.spring_backend_starter.service.dto.UserAuthDetails;
import com.bbsoft.spring_backend_starter.service.dto.user.UserPatch;
import com.bbsoft.spring_backend_starter.service.dto.user.UserRequest;
import com.bbsoft.spring_backend_starter.service.dto.user.UserDTO;
import com.bbsoft.spring_backend_starter.service.dto.user.UserResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", uses = RoleMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UserMapper {

    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "encodedPassword", source = "userRequest.password", qualifiedByName = "toEncodedPassword")
    public abstract User toUser(UserRequest userRequest, List<UserRole> roles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "encodedPassword", source = "userPatchRequest.password", qualifiedByName = "toEncodedPassword")
    public abstract User toUser(UserPatch userPatchRequest);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "authorities", source = "roles")
    public abstract UserAuthDetails toUserDetails(User user);

    public abstract UserDTO toUserDTO(User user);

    public abstract UserResponse toUserResponse(User user);

    @Named("toEncodedPassword")
    public String toEncodedPassword(String password) {
        if (Objects.isNull(password)) {
            return null;
        }
        return passwordEncoder.encode(password);
    }
}
