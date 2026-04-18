package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.enums.BugStatus;
import com.bug_reporter.backend.service.BugService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BugControllerTest {

    @Mock
    private BugService bugService;

    @InjectMocks
    private BugController bugController;

    private Bug testBug;

    @BeforeEach
    void setUp() {
        User author = new User();
        author.setId(1L);
        author.setUsername("tester");

        testBug = new Bug();
        testBug.setId(100L);
        testBug.setTitle("UI Error");
        testBug.setText("Button not working");
        testBug.setStatus(BugStatus.RECEIVED);
        testBug.setAuthor(author);
    }

    @Test
    void getBugs() {
        when(bugService.getFilteredBugs("UI", 1L, null)).thenReturn(List.of(testBug));
        ResponseEntity<List<Bug>> result = bugController.getBugs("UI", 1L, null);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getBugById() {
        when(bugService.findById(100L)).thenReturn(testBug);
        ResponseEntity<?> result = bugController.getBugById(100L);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void getBugById_notFound() {
        when(bugService.findById(99L)).thenThrow(new RuntimeException("Bug not found"));
        ResponseEntity<?> result = bugController.getBugById(99L);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void createBug() {
        when(bugService.save(any(Bug.class))).thenReturn(testBug);
        ResponseEntity<?> result = bugController.createBug(testBug);
        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    void updateBug() {
        when(bugService.updateBug(eq(100L), any(Bug.class), eq(1L))).thenReturn(testBug);
        ResponseEntity<?> result = bugController.updateBug(100L, testBug, 1L);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void updateBug_unauthorized() {
        ResponseEntity<?> result = bugController.updateBug(100L, testBug, null);
        assertEquals(401, result.getStatusCode().value());
    }

    @Test
    void updateBug_forbidden() {
        when(bugService.updateBug(eq(100L), any(Bug.class), eq(99L)))
                .thenThrow(new SecurityException("Not allowed"));
        ResponseEntity<?> result = bugController.updateBug(100L, testBug, 99L);
        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    void deleteBug() {
        ResponseEntity<?> result = bugController.deleteBug(100L, 1L);
        assertEquals(204, result.getStatusCode().value());
    }

    @Test
    void deleteBug_unauthorized() {
        ResponseEntity<?> result = bugController.deleteBug(100L, null);
        assertEquals(401, result.getStatusCode().value());
    }
}
