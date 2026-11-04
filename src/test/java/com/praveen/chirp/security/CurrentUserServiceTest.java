package com.praveen.chirp.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserServiceTest {
    private CurrentUserService currentUserService;

    @BeforeEach
    void setup() {
        currentUserService = new CurrentUserService();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsername_shouldReturnUsername_whenAuthenticated() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "chirptest", null, AuthorityUtils.createAuthorityList("ROLE_USER"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = currentUserService.getCurrentUser();

        assertEquals("chirptest", username);

    }

    @Test
    void getCurrentUsername_shouldThrowException_whenNoAuthentication() {
        SecurityContextHolder.clearContext();

        assertThrows(IllegalStateException.class, () -> currentUserService.getCurrentUser());
    }

    @Test
    void getCurrentUsername_shouldThrowException_whenAnonymous() {
        var annonymousAuth = new AnonymousAuthenticationToken(
                "test-user", "abcdef",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        SecurityContextHolder.getContext().setAuthentication(annonymousAuth);

        assertThrows(IllegalStateException.class, () -> currentUserService.getCurrentUser());
    }
}
