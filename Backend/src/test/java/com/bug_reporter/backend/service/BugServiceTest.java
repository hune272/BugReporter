package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.enums.BugStatus;
import com.bug_reporter.backend.repository.BugRepository;
import com.bug_reporter.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BugServiceTest {

    @Mock
    private BugRepository bugRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BugService bugService;

    private Bug testBug;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("Tester");

        testBug = new Bug();
        testBug.setId(100L);
        testBug.setTitle("Eroare la salvare");
        testBug.setText("Aplicatia pica cand salvez.");
        testBug.setStatus(BugStatus.RECEIVED);
        testBug.setCreatedAt(LocalDateTime.now());
        testBug.setAuthor(testUser);
    }

    @Test
    void findAll() {
        when(bugRepository.findAll()).thenReturn(List.of(testBug));

        List<Bug> bugs = bugService.findAll();
        assertNotNull(bugs);
        assertEquals(1, bugs.size());
        assertEquals(testBug, bugs.getFirst());
        assertEquals(testUser, bugs.getFirst().getAuthor());
        assertEquals("Eroare la salvare", bugs.getFirst().getTitle());

        verify(bugRepository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(bugRepository.findById(testBug.getId())).thenReturn(Optional.of(testBug));

        Bug bug = bugService.findById(testBug.getId());
        assertNotNull(bug);
        assertEquals(100L, bug.getId());
        verify(bugRepository, times(1)).findById(testBug.getId());
    }

    @Test
    void findById_notFound() {
        when(bugRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bugService.findById(99L));
    }

    @Test
    void save() {
        Bug newBug = new Bug();
        newBug.setTitle("New Bug");
        newBug.setText("Description");
        User author = new User();
        author.setId(1L);
        newBug.setAuthor(author);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bugRepository.save(any(Bug.class))).thenReturn(newBug);

        Bug result = bugService.save(newBug);

        assertEquals(BugStatus.RECEIVED, newBug.getStatus());
        assertNotNull(newBug.getCreatedAt());
        assertEquals(testUser, newBug.getAuthor());
        verify(bugRepository, times(1)).save(newBug);
    }

    @Test
    void save_authorNotFound() {
        Bug newBug = new Bug();
        User author = new User();
        author.setId(99L);
        newBug.setAuthor(author);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bugService.save(newBug));
    }

    @Test
    void getFilteredBugs_byAuthorId() {
        when(bugRepository.findByAuthorId(eq(1L), any(Sort.class))).thenReturn(List.of(testBug));

        List<Bug> result = bugService.getFilteredBugs(null, 1L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tester", result.getFirst().getAuthor().getUsername());
    }

    @Test
    void getFilteredBugs_byTitle() {
        when(bugRepository.findByTitleContainingIgnoreCase(eq("Eroare"), any(Sort.class)))
                .thenReturn(List.of(testBug));

        List<Bug> result = bugService.getFilteredBugs("Eroare", null, null);

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getTitle().contains("Eroare"));
    }

    @Test
    void updateBug() {
        when(bugRepository.findById(100L)).thenReturn(Optional.of(testBug));
        when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

        Bug updatedInfo = new Bug();
        updatedInfo.setTitle("Titlu modif");
        updatedInfo.setText("Text modif");

        Bug result = bugService.updateBug(100L, updatedInfo, 1L);
        assertNotNull(result);
        verify(bugRepository, times(1)).save(any(Bug.class));
    }

    @Test
    void updateBug_forbidden() {
        when(bugRepository.findById(100L)).thenReturn(Optional.of(testBug));

        Bug updatedInfo = new Bug();
        updatedInfo.setTitle("Titlu modif");

        assertThrows(SecurityException.class, () -> bugService.updateBug(100L, updatedInfo, 99L));
    }

    @Test
    void deleteBug() {
        when(bugRepository.findById(100L)).thenReturn(Optional.of(testBug));
        bugService.deleteBug(100L, 1L);
        verify(bugRepository, times(1)).delete(testBug);
    }

    @Test
    void deleteBug_forbidden() {
        when(bugRepository.findById(100L)).thenReturn(Optional.of(testBug));

        assertThrows(SecurityException.class, () -> bugService.deleteBug(100L, 99L));
    }
}