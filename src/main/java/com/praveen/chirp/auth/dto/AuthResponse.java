package com.praveen.chirp.auth.dto;

public record AuthResponse(
		String accessToken, String tokenType, long expiresIn) {
}
