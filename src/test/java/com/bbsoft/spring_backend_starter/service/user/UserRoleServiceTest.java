package com.bbsoft.spring_backend_starter.service.user;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.exception.EntityNotFoundException;
import com.bbsoft.spring_backend_starter.repository.UserRoleRepository;
import com.bbsoft.spring_backend_starter.repository.entity.UserRole;
import com.bbsoft.spring_backend_starter.service.mapper.RoleMapper;
import com.bbsoft.spring_backend_starter.service.mapper.RoleMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

    private static final Set<Role> ROLES = Set.of(Role.AUTHENTICATED_USER, Role.ADMIN, Role.SIMPLE_USER);

    @Mock
    private UserRoleRepository userRoleRepository;
    @Spy
    private final RoleMapper roleMapper = new RoleMapperImpl();
    @InjectMocks
    private UserRoleService userRoleService;

    @Test
    public void collectUserRoles_ShouldThrownExceptionWhenRoleNotFound() {
        // GIVEN
        var foundRoles = List.of(createUserRole(Role.AUTHENTICATED_USER), createUserRole(Role.ADMIN));
        Mockito.when(userRoleRepository.findUserRolesByRoleIn(ROLES)).thenReturn(foundRoles);
        // THEN
        var thrownException = Assertions.assertThrows(EntityNotFoundException.class, () -> userRoleService.collectUserRoles(ROLES));
        Assertions.assertEquals(ErrorCodes.ENTITY_NOT_FOUND, thrownException.getErrorCode());
    }

    @Test
    public void collectUserRoles_ShouldCollectAllRoles() {
        // GIVEN
        var foundRoles = ROLES.stream().map(roleMapper::toUserRole).toList();
        Mockito.when(userRoleRepository.findUserRolesByRoleIn(ROLES)).thenReturn(foundRoles);
        // WHEN
        var userRoles = userRoleService.collectUserRoles(ROLES);
        // THEN
        Assertions.assertEquals(ROLES, userRoles.stream().map(roleMapper::toRole).collect(Collectors.toSet()));
    }

    private UserRole createUserRole(Role role) {
        return UserRole.builder().role(role).build();
    }
}
