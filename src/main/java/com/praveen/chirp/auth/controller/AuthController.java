package com.praveen.chirp.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.praveen.chirp.auth.dto.AuthResponse;
import com.praveen.chirp.auth.dto.LoginRequest;
import com.praveen.chirp.auth.service.AuthService;
import com.praveen.chirp.security.CurrentUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final CurrentUserService currentUserService;

    AuthController(AuthService authService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = this.authService.login(request);
        return ResponseEntity.ok(response);
    }

    // @GetMapping("/me")
    // public ResponseEntity<Map<String, String>> getCurrentUser(
    // Authentication authentication) {
    // return ResponseEntity.ok(
    // Map.of("username", authentication.getName()));
    // }

    // @GetMapping("/me")
    // public ResponseEntity<Map<String, String>> getCurrentUser(
    // @AuthenticationPrincipal Jwt jwt) {
    // return ResponseEntity.ok(Map.of("username", jwt.getSubject()));
    // }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser() {
        return ResponseEntity.ok(
                Map.of("username", currentUserService.getCurrentUser()));
    }
}
