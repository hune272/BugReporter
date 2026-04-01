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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

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

        Bug result = bugController.getBugById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void createBug() throws Exception {
        List<Long> tags = List.of(5L, 6L);

        bugController.createBug(testBug, 1L, tags);
        verify(bugService, times(1)).save(testBug, 1L, tags);
    }

    @Test
    void updateBug() throws Exception {
        when(bugService.updateBug(eq(100L), any(Bug.class), eq(1L))).thenReturn(testBug);

        Bug result = bugController.updateBug(100L, testBug, 1L);
        assertNotNull(result);
        assertEquals("UI Error", result.getTitle());
    }

    @Test
    void deleteBug() throws Exception {
        String response = bugController.deleteBug(100L, 1L);

        assertEquals("Bug deleted", response);
        verify(bugService, times(1)).deleteBug(100L, 1L);
    }
}