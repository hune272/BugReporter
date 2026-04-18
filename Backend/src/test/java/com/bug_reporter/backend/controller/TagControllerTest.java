package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.service.TagService;
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
class TagControllerTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(1L);
        tag.setName("backend");
    }

    @Test
    void getAllTags() {
        when(tagService.findAll()).thenReturn(List.of(tag));
        ResponseEntity<List<Tag>> result = tagController.getAllTags();
        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getTagById() {
        when(tagService.findById(1L)).thenReturn(tag);
        ResponseEntity<?> result = tagController.getTagById(1L);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void getTagById_notFound() {
        when(tagService.findById(99L)).thenThrow(new RuntimeException("Tag not found"));
        ResponseEntity<?> result = tagController.getTagById(99L);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void getTagByName() {
        when(tagService.findByName("backend")).thenReturn(tag);
        ResponseEntity<?> result = tagController.getTagByName("backend");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void getTagByName_notFound() {
        when(tagService.findByName("nonexistent")).thenReturn(null);
        ResponseEntity<?> result = tagController.getTagByName("nonexistent");
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void addTag() {
        when(tagService.createTag(any(Tag.class))).thenReturn(tag);
        ResponseEntity<?> result = tagController.addTag(tag);
        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    void addTag_conflict() {
        when(tagService.createTag(any(Tag.class))).thenThrow(new IllegalStateException("Exists"));
        ResponseEntity<?> result = tagController.addTag(tag);
        assertEquals(409, result.getStatusCode().value());
    }

    @Test
    void updateTag() {
        Tag updatedTag = new Tag();
        updatedTag.setName("frontend");
        when(tagService.updateTag(eq(1L), any(Tag.class))).thenReturn(updatedTag);
        ResponseEntity<?> result = tagController.updateTag(1L, updatedTag);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void updateTag_notFound() {
        when(tagService.updateTag(eq(99L), any(Tag.class))).thenThrow(new RuntimeException("Not found"));
        ResponseEntity<?> result = tagController.updateTag(99L, tag);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void deleteTag() {
        doNothing().when(tagService).deleteTag(1L);
        ResponseEntity<?> result = tagController.deleteTag(1L);
        assertEquals(204, result.getStatusCode().value());
    }

    @Test
    void deleteTag_notFound() {
        doThrow(new RuntimeException("Not found")).when(tagService).deleteTag(99L);
        ResponseEntity<?> result = tagController.deleteTag(99L);
        assertEquals(404, result.getStatusCode().value());
    }
}
