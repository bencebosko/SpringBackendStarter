package com.bbsoft.spring_backend_starter.repository;

import com.bbsoft.spring_backend_starter.repository.entity.UserSettings;
import com.bbsoft.spring_backend_starter.repository.projection.UserLocale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

    Optional<UserLocale> findLocaleByUserId(Long userId);
}
