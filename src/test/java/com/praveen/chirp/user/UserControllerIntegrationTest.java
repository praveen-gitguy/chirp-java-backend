package com.praveen.chirp.user;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.praveen.chirp.user.dto.CreateUserRequest;
import com.praveen.chirp.user.entity.User;
import com.praveen.chirp.user.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerUser_shouldReturn201_whenRequestIsValid() throws Exception {
        CreateUserRequest request = new CreateUserRequest("integration_user", "integration@example.com",
                "StrongTestPassword123!");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("integration_user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("integration@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.passwordHash").doesNotExist());

    }

    @Test
    void registerUser_shouldStoreHashedPassword_whenRequestIsValid()
            throws Exception {

        CreateUserRequest request = new CreateUserRequest(
                "hash_test_user",
                "hash_test@example.com",
                "StrongTestPassword123!");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        User user = userRepository.findByUsername("hash_test_user")
                .orElseThrow();

        assertNotEquals(request.password(), user.getPasswordHash());

        assertTrue(
                passwordEncoder.matches(
                        request.password(),
                        user.getPasswordHash()));
    }

    @Test
    void registerUser_shouldReturn409_whenUsernameAlreadyExists()
            throws Exception {

        CreateUserRequest request = new CreateUserRequest(
                "duplicate_user",
                "first@example.com",
                "StrongTestPassword123!");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        CreateUserRequest duplicateRequest = new CreateUserRequest(
                "duplicate_user",
                "second@example.com",
                "AnotherStrongPassword123!");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void registerUser_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        String invalidRequest = """
                {
                    "username": "",
                    "email": "invalid-email",
                    "password": "short"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
