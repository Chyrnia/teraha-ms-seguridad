package com.teraha.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teraha.commons.entities.settings.UserAppearanceSettings;

@Repository
public interface UserAppearanceSettingsRepository
        extends JpaRepository<UserAppearanceSettings, Long> {
}
