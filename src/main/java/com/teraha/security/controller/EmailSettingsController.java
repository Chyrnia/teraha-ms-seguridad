package com.teraha.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teraha.commons.dtos.EmailSettingsDTO;
import com.teraha.commons.dtos.UpdateEmailSettingsDTO;
import com.teraha.security.service.EmailSettingsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/settings/email")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class EmailSettingsController {

    private final EmailSettingsService emailSettingsService;

    @GetMapping
    @Operation(summary = "Get global email settings")
    public ResponseEntity<EmailSettingsDTO> getSettings() {
        return ResponseEntity.ok(emailSettingsService.getSettings());
    }

    @PutMapping
    @Operation(summary = "Update global email settings")
    public ResponseEntity<EmailSettingsDTO> updateSettings(
            @Valid @RequestBody UpdateEmailSettingsDTO request) {

        return ResponseEntity.ok(
            emailSettingsService.updateSettings(request)
        );
    }
}
