package com.bbsoft.spring_backend_starter.repository;

import com.bbsoft.spring_backend_starter.repository.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    Optional<UserAuth> findByUserId(Long userId);
}
