package com.teraha.security.service;

import java.util.Objects;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teraha.commons.dtos.AppearanceSettingsDTO;
import com.teraha.commons.dtos.UpdateAppearanceSettingsDTO;
import com.teraha.commons.entities.User;
import com.teraha.commons.entities.settings.UserAppearanceSettings;
import com.teraha.commons.entities.settings.enums.AccentColor;
import com.teraha.commons.entities.settings.enums.SidebarBehavior;
import com.teraha.commons.entities.settings.enums.TableDensity;
import com.teraha.security.exception.UserNotFoundException;
import com.teraha.security.repository.UserAppearanceSettingsRepository;
import com.teraha.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
@PreAuthorize("isAuthenticated()")
public class AppearanceSettingsService {

    private final UserAppearanceSettingsRepository settingsRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppearanceSettingsDTO getSettings(String username) {
        UserAppearanceSettings settings = findOrCreateSettings(username);
        return toDto(settings);
    }

    public AppearanceSettingsDTO updateSettings(
            String username,
            UpdateAppearanceSettingsDTO dto) {

        UserAppearanceSettings settings = findOrCreateSettings(username);

        settings.setAccentColor(
            AccentColor.valueOf(dto.getAccentColor().toString())
        );

        settings.setTableDensity(
            TableDensity.valueOf(dto.getTableDensity().toString())
        );

        settings.setSidebarBehavior(
            SidebarBehavior.valueOf(dto.getSidebarBehavior().toString())
        );

        return toDto(settingsRepository.save(settings));
    }

    private UserAppearanceSettings findOrCreateSettings(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

        return settingsRepository.findById(user.getId())
            .orElseGet(() -> settingsRepository.save(
                new UserAppearanceSettings(user)
            ));
    }

    private AppearanceSettingsDTO toDto(
            UserAppearanceSettings settings) {

        AppearanceSettingsDTO dto = new AppearanceSettingsDTO();

        dto.setAccentColor(
            AppearanceSettingsDTO.AccentColorEnum.fromValue(
                settings.getAccentColor().name()
            )
        );

        dto.setTableDensity(
            AppearanceSettingsDTO.TableDensityEnum.fromValue(
                settings.getTableDensity().name()
            )
        );

        dto.setSidebarBehavior(
            AppearanceSettingsDTO.SidebarBehaviorEnum.fromValue(
                settings.getSidebarBehavior().name()
            )
        );

        return dto;
    }
}
