package com.teraha.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teraha.commons.dtos.AppearanceSettingsDTO;
import com.teraha.commons.dtos.UpdateAppearanceSettingsDTO;
import com.teraha.security.service.AppearanceSettingsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/settings/appearance")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AppearanceSettingsController {

    private final AppearanceSettingsService appearanceSettingsService;

    @GetMapping
    @Operation(summary = "Get the authenticated user's appearance settings")
    public ResponseEntity<AppearanceSettingsDTO> getSettings(
            Authentication authentication) {

        return ResponseEntity.ok(
            appearanceSettingsService.getSettings(authentication.getName())
        );
    }

    @PutMapping
    @Operation(summary = "Update the authenticated user's appearance settings")
    public ResponseEntity<AppearanceSettingsDTO> updateSettings(
            Authentication authentication,
            @Valid @RequestBody UpdateAppearanceSettingsDTO request) {

        return ResponseEntity.ok(
            appearanceSettingsService.updateSettings(
                authentication.getName(),
                request
            )
        );
    }
}
