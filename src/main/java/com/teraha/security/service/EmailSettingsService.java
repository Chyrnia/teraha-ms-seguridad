package com.teraha.security.service;

import java.util.List;
import java.util.Locale;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teraha.commons.dtos.EmailSettingsDTO;
import com.teraha.commons.dtos.UpdateEmailSettingsDTO;
import com.teraha.commons.entities.settings.EmailRecipient;
import com.teraha.commons.entities.settings.EmailSettings;
import com.teraha.commons.entities.settings.enums.EmailFrequency;
import com.teraha.security.repository.EmailSettingsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
@PreAuthorize("hasRole('SYSADMIN')")
public class EmailSettingsService {

    private static final Long SETTINGS_ID = 1L;

    private final EmailSettingsRepository emailSettingsRepository;

    @Transactional(readOnly = true)
    public EmailSettingsDTO getSettings() {
        EmailSettings settings = findSettings();
        return toDto(settings);
    }

    public EmailSettingsDTO updateSettings(UpdateEmailSettingsDTO dto) {
        EmailSettings settings = findSettings();

        settings.setSenderEmail(
            dto.getSenderEmail().trim().toLowerCase(Locale.ROOT)
        );

        settings.setFrequency(
            EmailFrequency.valueOf(dto.getFrequency().toString())
        );

        settings.setDefaultMessage(dto.getDefaultMessage());

        settings.setIncludeLowStock(dto.getIncludeLowStock());
        settings.setIncludeSalesMetrics(dto.getIncludeSalesMetrics());

        settings.replaceRecipients(normalizeRecipients(dto.getRecipients()));

        return toDto(emailSettingsRepository.save(settings));
    }

    private EmailSettings findSettings() {
        return emailSettingsRepository.findById(SETTINGS_ID)
            .orElseThrow(() -> new IllegalStateException(
                "Global email settings have not been initialized"
            ));
    }

    private List<String> normalizeRecipients(List<String> recipients) {
        if (recipients == null) {
            throw new IllegalArgumentException(
                "Recipient list must not be null"
            );
        }

        List<String> normalized = recipients.stream()
            .map(email -> {
                if (email == null || email.isBlank()) {
                    throw new IllegalArgumentException(
                        "Recipient email must not be blank"
                    );
                }

                return email.trim().toLowerCase(Locale.ROOT);
            })
            .toList();

        if (normalized.stream().distinct().count() != normalized.size()) {
            throw new IllegalArgumentException(
                "Duplicate recipient emails are not allowed"
            );
        }

        return normalized;
    }

    private EmailSettingsDTO toDto(EmailSettings settings) {
        EmailSettingsDTO dto = new EmailSettingsDTO();

        dto.setSenderEmail(settings.getSenderEmail());
        dto.setFrequency(
            EmailSettingsDTO.FrequencyEnum.fromValue(
                settings.getFrequency().name()
            )
        );
        dto.setRecipients(
            settings.getRecipients().stream()
                .map(EmailRecipient::getEmail)
                .toList()
        );
        dto.setDefaultMessage(settings.getDefaultMessage());
        dto.setIncludeLowStock(settings.isIncludeLowStock());
        dto.setIncludeSalesMetrics(settings.isIncludeSalesMetrics());

        return dto;
    }
}
