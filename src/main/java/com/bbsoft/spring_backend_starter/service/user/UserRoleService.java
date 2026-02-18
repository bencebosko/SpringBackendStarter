package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.exception.EntityNotFoundException;
import com.bbsoft.spring_backend_starter.repository.UserRoleRepository;
import com.bbsoft.spring_backend_starter.repository.entity.UserRole;
import com.bbsoft.spring_backend_starter.service.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<UserRole> collectUserRoles(Set<Role> roles) {
        final var savedUserRoles = userRoleRepository.findUserRolesByRoleIn(roles);
        if (!Objects.equals(savedUserRoles.size(), roles.size())) {
            final var foundRoles = savedUserRoles.stream().map(roleMapper::toRole).collect(Collectors.toSet());
            final var notFoundRoles = new HashSet<>(roles);
            notFoundRoles.removeIf(foundRoles::contains);
            throw EntityNotFoundException.createUserRoleNotFound(notFoundRoles);
        }
        return savedUserRoles;
    }
}
