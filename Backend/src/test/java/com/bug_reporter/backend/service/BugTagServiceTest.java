package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.BugTag;
import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.repository.BugRepository;
import com.bug_reporter.backend.repository.BugTagRepository;
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
public class BugTagServiceTest {

    @Mock
    private BugTagRepository bugTagRepository;

    @Mock
    private BugRepository bugRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private BugTagService bugTagService;

    private Bug testBug;
    private Tag testTag;
    private BugTag testBugTag;

    @BeforeEach
    void setUp() {
        testBug = new Bug();
        testBug.setId(1L);
        testBug.setTitle("Test bug");

        testTag = new Tag();
        testTag.setId(10L);
        testTag.setName("UI");

        testBugTag = new BugTag();
        testBugTag.setId(100L);
        testBugTag.setBug(testBug);
        testBugTag.setTag(testTag);
    }

    @Test
    void getTagsByBugId() {
        when(bugTagRepository.findByBugId(1L)).thenReturn(List.of(testBugTag));

        List<Tag> result = bugTagService.getTagsByBugId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("UI", result.get(0).getName());
        verify(bugTagRepository, times(1)).findByBugId(1L);
    }

    @Test
    void addTagToBug() {
        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
        when(tagRepository.findById(10L)).thenReturn(Optional.of(testTag));
        when(bugTagRepository.findByBugAndTag(testBug, testTag)).thenReturn(Optional.empty());
        when(bugTagRepository.save(any(BugTag.class))).thenReturn(testBugTag);

        BugTag result = bugTagService.addTagToBug(1L, 10L);

        assertNotNull(result);
        assertEquals(testBug, result.getBug());
        assertEquals(testTag, result.getTag());
        verify(bugTagRepository, times(1)).save(any(BugTag.class));
    }

    @Test
    void addTagToBug_bugNotFound() {
        when(bugRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bugTagService.addTagToBug(99L, 10L));
    }

    @Test
    void addTagToBug_tagNotFound() {
        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bugTagService.addTagToBug(1L, 99L));
    }

    @Test
    void addTagToBug_alreadyAssigned() {
        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
        when(tagRepository.findById(10L)).thenReturn(Optional.of(testTag));
        when(bugTagRepository.findByBugAndTag(testBug, testTag)).thenReturn(Optional.of(testBugTag));

        assertThrows(RuntimeException.class, () -> bugTagService.addTagToBug(1L, 10L));
    }

    @Test
    void removeTagFromBug() {
        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
        when(tagRepository.findById(10L)).thenReturn(Optional.of(testTag));
        when(bugTagRepository.findByBugAndTag(testBug, testTag)).thenReturn(Optional.of(testBugTag));

        bugTagService.removeTagFromBug(1L, 10L);

        verify(bugTagRepository, times(1)).delete(testBugTag);
    }

    @Test
    void removeTagFromBug_bugNotFound() {
        when(bugRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bugTagService.removeTagFromBug(99L, 10L));
    }

    @Test
    void removeTagFromBug_notAssigned() {
        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
        when(tagRepository.findById(10L)).thenReturn(Optional.of(testTag));
        when(bugTagRepository.findByBugAndTag(testBug, testTag)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bugTagService.removeTagFromBug(1L, 10L));
    }

    @Test
    void removeAllTagsFromBug() {
        when(bugRepository.existsById(1L)).thenReturn(true);

        bugTagService.removeAllTagsFromBug(1L);

        verify(bugTagRepository, times(1)).deleteByBugId(1L);
    }

    @Test
    void removeAllTagsFromBug_bugNotFound() {
        when(bugRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> bugTagService.removeAllTagsFromBug(99L));
    }
}