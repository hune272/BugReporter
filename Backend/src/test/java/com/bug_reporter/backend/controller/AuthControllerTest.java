package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.enums.UserRole;
import com.bug_reporter.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.USER);
    }

    @Test
    void register() {
        when(authService.register("testUser", "test@example.com", "password123")).thenReturn(testUser);

        Map<String, String> body = Map.of(
                "username", "testUser",
                "email", "test@example.com",
                "password", "password123"
        );

        ResponseEntity<?> result = authController.register(body);

        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody());
        verify(authService, times(1)).register("testUser", "test@example.com", "password123");
    }

    @Test
    void register_error() {
        when(authService.register("testUser", "test@example.com", "password123"))
                .thenThrow(new RuntimeException("Email already exists"));

        Map<String, String> body = Map.of(
                "username", "testUser",
                "email", "test@example.com",
                "password", "password123"
        );

        ResponseEntity<?> result = authController.register(body);

        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    void login() {
        when(authService.login("test@example.com", "password123", request, response)).thenReturn(testUser);

        Map<String, String> body = Map.of(
                "email", "test@example.com",
                "password", "password123"
        );

        ResponseEntity<?> result = authController.login(body, request, response);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        verify(authService, times(1)).login("test@example.com", "password123", request, response);
    }

    @Test
    void login_invalidCredentials() {
        when(authService.login("wrong@example.com", "wrongPass", request, response))
                .thenThrow(new RuntimeException("Invalid email or password"));

        Map<String, String> body = Map.of(
                "email", "wrong@example.com",
                "password", "wrongPass"
        );

        ResponseEntity<?> result = authController.login(body, request, response);

        assertEquals(401, result.getStatusCode().value());
    }

    @Test
    void logout() {
        doNothing().when(authService).logout(request);

        ResponseEntity<Void> result = authController.logout(request);

        assertEquals(204, result.getStatusCode().value());
        verify(authService, times(1)).logout(request);
    }
}