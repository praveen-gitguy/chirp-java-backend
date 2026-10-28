package com.praveen.chirp.auth.dto;

public record AuthResponse(
        String accessToken, String refreshToken, long expiresIn) {
}
