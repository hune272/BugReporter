package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.enums.UserRole;
import com.bug_reporter.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

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
    void getAllUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));

        ResponseEntity<List<User>> result = userController.getAllUsers();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<User> result = userController.getUserById(1L);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("testUser", result.getBody().getUsername());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_notFound() {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        ResponseEntity<User> result = userController.getUserById(99L);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void updateUser() {
        User updatedUser = new User();
        updatedUser.setUsername("updatedName");

        when(userService.updateUser(1L, updatedUser)).thenReturn(updatedUser);

        ResponseEntity<User> result = userController.updateUser(1L, updatedUser);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("updatedName", result.getBody().getUsername());
        verify(userService, times(1)).updateUser(1L, updatedUser);
    }

    @Test
    void updateUser_notFound() {
        when(userService.updateUser(eq(99L), any(User.class))).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<User> result = userController.updateUser(99L, testUser);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void deleteUser() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> result = userController.deleteUser(1L);

        assertEquals(204, result.getStatusCode().value());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_notFound() {
        doThrow(new RuntimeException("User not found")).when(userService).deleteUser(99L);

        ResponseEntity<Void> result = userController.deleteUser(99L);

        assertEquals(404, result.getStatusCode().value());
    }
}