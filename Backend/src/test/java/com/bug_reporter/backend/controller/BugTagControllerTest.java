package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.BugTag;
import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.service.BugTagService;
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
class BugTagControllerTest {

    @Mock
    private BugTagService bugTagService;

    @InjectMocks
    private BugTagController bugTagController;

    private Tag testTag;

    @BeforeEach
    void setUp() {
        testTag = new Tag();
        testTag.setId(10L);
        testTag.setName("UI");
    }

    @Test
    void getTagsByBugId() {
        when(bugTagService.getTagsByBugId(1L)).thenReturn(List.of(testTag));

        ResponseEntity<List<Tag>> result = bugTagController.getTagsByBugId(1L);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("UI", result.getBody().get(0).getName());
        verify(bugTagService, times(1)).getTagsByBugId(1L);
    }

    @Test
    void addTagToBug() {
        BugTag bugTag = new BugTag();
        when(bugTagService.addTagToBug(1L, 10L)).thenReturn(bugTag);

        ResponseEntity<?> result = bugTagController.addTagToBug(1L, 10L);

        assertEquals(201, result.getStatusCode().value());
        verify(bugTagService, times(1)).addTagToBug(1L, 10L);
    }

    @Test
    void addTagToBug_notFound() {
        when(bugTagService.addTagToBug(99L, 10L)).thenThrow(new RuntimeException("Bug not found"));

        ResponseEntity<?> result = bugTagController.addTagToBug(99L, 10L);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void removeTagFromBug() {
        doNothing().when(bugTagService).removeTagFromBug(1L, 10L);

        ResponseEntity<?> result = bugTagController.removeTagFromBug(1L, 10L);

        assertEquals(204, result.getStatusCode().value());
        verify(bugTagService, times(1)).removeTagFromBug(1L, 10L);
    }

    @Test
    void removeTagFromBug_notFound() {
        doThrow(new RuntimeException("Tag not assigned")).when(bugTagService).removeTagFromBug(99L, 10L);

        ResponseEntity<?> result = bugTagController.removeTagFromBug(99L, 10L);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void removeAllTagsFromBug() {
        doNothing().when(bugTagService).removeAllTagsFromBug(1L);

        ResponseEntity<?> result = bugTagController.removeAllTagsFromBug(1L);

        assertEquals(204, result.getStatusCode().value());
        verify(bugTagService, times(1)).removeAllTagsFromBug(1L);
    }

    @Test
    void removeAllTagsFromBug_notFound() {
        doThrow(new RuntimeException("Bug not found")).when(bugTagService).removeAllTagsFromBug(99L);

        ResponseEntity<?> result = bugTagController.removeAllTagsFromBug(99L);

        assertEquals(404, result.getStatusCode().value());
    }
}