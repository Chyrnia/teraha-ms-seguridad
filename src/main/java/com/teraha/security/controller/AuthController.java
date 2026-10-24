package com.teraha.security.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.teraha.commons.dtos.LoginRequestDTO;
import com.teraha.commons.dtos.LoginResponseDTO;
import com.teraha.security.service.JwtService;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDTO request) {

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
        } catch (AuthenticationException ex) {
            log.warn("Authentication failed");
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                    "status", 401,
                    "message", "Invalid username or password",
                    "timestamp", Instant.now().toString()
                ));
        }

        JwtService.IssuedToken issued =
            jwtService.issueToken(authentication);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setAccessToken(issued.value());
        response.setTokenType("Bearer");
        response.setExpiresIn(issued.expiresInSeconds());

        return ResponseEntity.ok(response);
    }
}
