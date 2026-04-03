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
class BugServiceTest {

    @Mock
    private BugRepository bugRepository;

    @InjectMocks
    private BugService bugService;
    @Mock
    private UserRepository userRepository;


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
        assertEquals("Eroare la salvare",  bugs.getFirst().getTitle());

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
    void save() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        bugService.save(testBug, testUser.getId(), null);

        assertEquals(BugStatus.RECEIVED, testBug.getStatus());
        assertNotNull(testBug.getCreatedAt());
        assertEquals(testUser, testBug.getAuthor());

        verify(bugRepository, times(1)).save(testBug);
    }

    @Test
    void getAllBugsByAuthorId() {
        when(bugRepository.getAllBugsByAuthorId(1L)).thenReturn(List.of(testBug));

        List<Bug> result = bugService.getAllBugsByAuthorId(1L);
        assertNotNull(result);
        assertEquals("Tester", result.getFirst().getAuthor().getUsername());
    }

    @Test
    void getAllBugsByTitle() {
        when(bugRepository.getAllBugsByTitle("Eroare")).thenReturn(List.of(testBug));

        List<Bug> result = bugService.getAllBugsByTitle("Eroare");

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getTitle().contains("Eroare"));
    }

    @Test
    void delete() {
        bugService.delete(testBug);
        verify(bugRepository, times(1)).delete(testBug);
    }

    @Test
    void getFilteredBugs() {
    }

    @Test
    void updateBug() {
        when(bugRepository.findById(100L)).thenReturn(Optional.of(testBug));

        when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

        Bug updatedInfo = new Bug();
        updatedInfo.setTitle("Titlu modif");
        updatedInfo.setStatus(BugStatus.IN_PROGRESS);

        Bug result = bugService.updateBug(100L, updatedInfo);
        assertNotNull(result);
        verify(bugRepository, times(1)).save(any(Bug.class));
    }

    @Test
    void deleteBug() {
        when(bugRepository.findById(100L)).thenReturn(Optional.of(testBug));
        bugService.deleteBug(100L);
        verify(bugRepository, times(1)).delete(testBug);
    }
}
