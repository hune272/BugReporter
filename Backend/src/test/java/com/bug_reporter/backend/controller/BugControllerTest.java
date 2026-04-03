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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void getBugs() throws Exception {
        when(bugService.getFilteredBugs("UI", 1L, null)).thenReturn(List.of(testBug));
        List<Bug> result = bugController.getBugs("UI", 1L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("UI Error", result.getFirst().getTitle());
    }

    @Test
    void getBugById() throws Exception {
        when(bugService.findById(100L)).thenReturn(testBug);

        ResponseEntity<Bug> result = bugController.getBugById(100L);

        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(100L, result.getBody().getId());
    }

    @Test
    void createBug() throws Exception {
        Map<String, Object> body = Map.of(
                "title", "UI Error",
                "text", "Button not working",
                "picture", "image.png",
                "authorId", 1L,
                "tagIds", List.of(5L, 6L)
        );
        when(bugService.save("UI Error", "Button not working", "image.png", 1L, List.of(5L, 6L)))
                .thenReturn(testBug);

        ResponseEntity<?> result = bugController.createBug(body);

        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(201), result.getStatusCode());
        verify(bugService, times(1)).save("UI Error", "Button not working", "image.png", 1L, List.of(5L, 6L));
    }

    @Test
    void updateBug() throws Exception {
        Map<String, Object> body = Map.of(
                "title", "UI Error",
                "text", "Button not working",
                "picture", "image.png",
                "status", "IN_PROGRESS",
                "tagIds", List.of(5L, 6L)
        );
        when(bugService.updateBug(100L, "UI Error", "Button not working", "image.png",
                BugStatus.IN_PROGRESS, List.of(5L, 6L))).thenReturn(testBug);

        ResponseEntity<?> result = bugController.updateBug(100L, body);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
    }

    @Test
    void deleteBug() throws Exception {
        ResponseEntity<?> response = bugController.deleteBug(100L);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        verify(bugService, times(1)).deleteBug(100L);
    }
}
