package com.bbsoft.spring_backend_starter.service.mapper;

import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.repository.entity.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RoleMapperTest {

    private static final Role ROLE = Role.SIMPLE_USER;
    private final RoleMapper roleMapper = new RoleMapperImpl();

    @Test
    public void toUserRole_ShouldMapNullRoleToNull() {
        Assertions.assertNull(roleMapper.toUserRole(null));
    }

    @Test
    public void toUserRole_ShouldMapRoleToUserRole() {
        // WHEN
        var userRole = roleMapper.toUserRole(ROLE);
        // THEN
        Assertions.assertEquals(ROLE, userRole.getRole());
    }

    @Test
    public void toRole_ShouldMapNullUserRoleToNull() {
        Assertions.assertNull(roleMapper.toRole(null));
    }

    @Test
    public void toRole_ShouldMapUserRoleToRole() {
        // GIVEN
        var userRole = createUserRole();
        // WHEN
        var role = roleMapper.toRole(userRole);
        // THEN
        Assertions.assertEquals(userRole.getRole(), role);
    }

    @Test
    public void toAuthority_ShouldMapNullRoleToNull() {
        Assertions.assertNull(roleMapper.toAuthority((Role) null));
    }

    @Test
    public void toAuthority_ShouldMapRoleToGrantedAuthority() {
        // GIVEN
        var expectedAuthority = RoleMapper.ROLE_PREFIX + ROLE.getName();
        // WHEN
        var authority = roleMapper.toAuthority(ROLE);
        // THEN
        Assertions.assertEquals(expectedAuthority, authority.getAuthority());
    }

    @Test
    public void toAuthority_ShouldMapNullUserRoleToNull() {
        Assertions.assertNull(roleMapper.toAuthority((UserRole) null));
    }

    @Test
    public void toAuthority_ShouldMapUserRoleToGrantedAuthority() {
        // GIVEN
        var userRole = createUserRole();
        var expectedAuthority = RoleMapper.ROLE_PREFIX + ROLE.getName();
        // WHEN
        var authority = roleMapper.toAuthority(userRole);
        // THEN
        Assertions.assertEquals(expectedAuthority, authority.getAuthority());
    }

    private UserRole createUserRole() {
        return UserRole.builder().role(ROLE).build();
    }
}
