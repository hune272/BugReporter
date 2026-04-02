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
        assertEquals(tag, result.getFirst());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Tag result = tagService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    void save() {
        when(tagRepository.save(tag)).thenReturn(tag);

        Tag result = tagService.save(tag);

        assertNotNull(result);
        assertEquals("backend", result.getName());
        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void delete() {
        tagService.delete(tag);

        verify(tagRepository, times(1)).delete(tag);
    }

    @Test
    void findByName() {
        when(tagRepository.findByNameIgnoreCase("backend")).thenReturn(Optional.of(tag));

        Tag result = tagService.findByName("backend");

        assertNotNull(result);
        assertEquals(tag, result);
        verify(tagRepository, times(1)).findByNameIgnoreCase("backend");
    }

    @Test
    void existsByNameIgnoreCase() {
        when(tagRepository.existsByNameIgnoreCase("backend")).thenReturn(true);

        boolean result = tagService.existsByNameIgnoreCase("backend");

        assertTrue(result);
        verify(tagRepository, times(1)).existsByNameIgnoreCase("backend");
    }
}