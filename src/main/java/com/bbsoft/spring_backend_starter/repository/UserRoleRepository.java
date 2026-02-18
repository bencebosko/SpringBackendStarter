package com.bbsoft.spring_backend_starter.repository;

import com.bbsoft.spring_backend_starter.constant.Role;
import com.bbsoft.spring_backend_starter.repository.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findUserRolesByRoleIn(Set<Role> roles);
}
