package com.bbsoft.spring_backend_starter.repository;

import com.bbsoft.spring_backend_starter.repository.entity.User;
import com.bbsoft.spring_backend_starter.repository.projection.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<UserPassword> findUserPasswordById(Long userId);

    void deleteByUsername(String username);
}
