package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        List<Tag> result = tagController.getAllTags();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(tag, result.getFirst());
    }

    @Test
    void getTagById() {
        when(tagService.findById(1L)).thenReturn(tag);

        Tag result = tagController.getTagById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTagByName() {
        when(tagService.findByName("backend")).thenReturn(tag);

        Tag result = tagController.getTagByName("backend");

        assertNotNull(result);
        assertEquals("backend", result.getName());
    }

    @Test
    void addTag() {
        when(tagService.existsByNameIgnoreCase("backend")).thenReturn(false);
        when(tagService.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tag result = tagController.addTag(tag);

        assertNotNull(result);
        assertEquals("backend", result.getName());
        verify(tagService, times(1)).save(tag);
    }

    @Test
    void updateTag() {
        Tag updatedTag = new Tag();
        updatedTag.setName("frontend");

        when(tagService.findById(1L)).thenReturn(tag);
        when(tagService.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tag result = tagController.updateTag(1L, updatedTag);

        assertNotNull(result);
        assertEquals("frontend", result.getName());
        verify(tagService, times(1)).save(tag);
    }

    @Test
    void deleteTag() {
        when(tagService.findById(1L)).thenReturn(tag);

        String result = tagController.deleteTag(1L);

        assertEquals("Tag deleted successfully", result);
        verify(tagService, times(1)).delete(tag);
    }
}
