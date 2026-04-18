package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(1L);
        tag.setName("backend");
    }

    @Test
    void findAll() {
        when(tagRepository.findAll()).thenReturn(List.of(tag));
        List<Tag> result = tagService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        Tag result = tagService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_notFound() {
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> tagService.findById(99L));
    }

    @Test
    void createTag() {
        when(tagRepository.existsByNameIgnoreCase("backend")).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        Tag result = tagService.createTag(tag);
        assertNotNull(result);
        assertEquals("backend", result.getName());
        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void createTag_duplicate() {
        when(tagRepository.existsByNameIgnoreCase("backend")).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> tagService.createTag(tag));
    }

    @Test
    void updateTag() {
        Tag updatedTag = new Tag();
        updatedTag.setName("frontend");
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        Tag result = tagService.updateTag(1L, updatedTag);
        assertNotNull(result);
        assertEquals("frontend", result.getName());
    }

    @Test
    void deleteTag() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        tagService.deleteTag(1L);
        verify(tagRepository, times(1)).delete(tag);
    }

    @Test
    void findByName() {
        when(tagRepository.findByNameIgnoreCase("backend")).thenReturn(Optional.of(tag));
        Tag result = tagService.findByName("backend");
        assertNotNull(result);
        assertEquals(tag, result);
    }

    @Test
    void existsByNameIgnoreCase() {
        when(tagRepository.existsByNameIgnoreCase("backend")).thenReturn(true);
        assertTrue(tagService.existsByNameIgnoreCase("backend"));
    }
}