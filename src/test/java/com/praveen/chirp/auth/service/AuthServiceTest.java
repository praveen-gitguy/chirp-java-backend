package com.praveen.chirp.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import com.praveen.chirp.auth.dto.AuthResponse;
import com.praveen.chirp.auth.dto.LoginRequest;

public class AuthServiceTest {
    private AuthenticationManager authenticationManager;
    private JwtEncoder jwtEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtEncoder = mock(JwtEncoder.class);

        authService = new AuthService(authenticationManager, jwtEncoder, 900);
    }

    @Test
    void login_shouldReturnAccessToken_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("chirptest", "StrongTestPassword123!");
        Authentication authentication = new UsernamePasswordAuthenticationToken("chirptest", null);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue()).thenReturn("mock-access-token");
        when(jwtEncoder.encode(any())).thenReturn(jwt);

        AuthResponse response = authService.login(request);

        assertEquals("mock-access-token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(900, response.expiresIn());

        verify(authenticationManager).authenticate(any());
        verify(jwtEncoder).encode(any());
    }

    @Test
    void login_shouldThrowException_whenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest("chirptest", "chirptest");

        when(authenticationManager.authenticate(any())).thenThrow(
                new BadCredentialsException("Bad Credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(jwtEncoder, never()).encode(any());
    }
}
