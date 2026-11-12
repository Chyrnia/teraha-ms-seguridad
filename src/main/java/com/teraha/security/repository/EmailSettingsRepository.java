package com.teraha.security.repository;

import org.springframework.stereotype.Repository;

import com.teraha.commons.entities.settings.EmailSettings;

@Repository
public interface EmailSettingsRepository
        extends BaseRepository<EmailSettings> {
    // The global configuration always has ID = 1.
}

