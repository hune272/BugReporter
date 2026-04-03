package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.enums.UserRole;
import com.bug_reporter.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.USER);
    }

    @Test
    void register() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        User result = authService.register("testUser", "test@example.com", "password123");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void register_nullUsername() {
        assertThrows(RuntimeException.class, () -> authService.register(null, "test@example.com", "password"));
    }

    @Test
    void register_emptyEmail() {
        assertThrows(RuntimeException.class, () -> authService.register("user", "", "password"));
    }

    @Test
    void register_emptyPassword() {
        assertThrows(RuntimeException.class, () -> authService.register("user", "test@example.com", ""));
    }

    @Test
    void login() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(request.getSession(true)).thenReturn(session);

        User result = authService.login("test@example.com", "password123", request, response);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(session).setAttribute("userId", 1L);
        verify(session).setAttribute("userEmail", "test@example.com");
        verify(session).setAttribute("userRole", "USER");
    }

    @Test
    void login_invalidEmail() {
        when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.login("wrong@example.com", "password", request, response));
    }

    @Test
    void login_invalidPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> authService.login("test@example.com", "wrongPass", request, response));
    }

    @Test
    void logout() {
        when(request.getSession(false)).thenReturn(session);

        authService.logout(request);

        verify(session, times(1)).invalidate();
    }

    @Test
    void logout_noSession() {
        when(request.getSession(false)).thenReturn(null);

        authService.logout(request);

        verify(session, never()).invalidate();
    }
}