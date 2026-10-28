package com.praveen.chirp.auth.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.praveen.chirp.auth.dto.AuthResponse;
import com.praveen.chirp.auth.dto.LoginRequest;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final long tokenTtlSeconds;

    AuthService(
            AuthenticationManager authenticationManager, JwtEncoder jwtEncoder,
            @Value("${security.jwt.access-token-ttl-seconds}") long tokenTtlSeconds) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("chirp")
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(tokenTtlSeconds))
                .claim("scope", "USER")
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        org.springframework.security.oauth2.jwt.JwsHeader
                                .with(MacAlgorithm.HS256)
                                .build(),
                        claims))
                .getTokenValue();

        return new AuthResponse(token, "Bearer", tokenTtlSeconds);
    }

}
