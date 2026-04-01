package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.model.enums.VoteType;
import com.bug_reporter.backend.service.VoteService;
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
class VoteControllerTest {

    @Mock
    private VoteService voteService;

    @InjectMocks
    private VoteController voteController;

    private Vote testVote;

    @BeforeEach
    void setUp() {
        testVote = new Vote();
        testVote.setId(1L);
        testVote.setType(VoteType.UPVOTE);
    }

    @Test
    void findAll() {
        when(voteService.findAll()).thenReturn(List.of(testVote));

        List<Vote> result = voteController.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(voteService, times(1)).findAll();
    }

    @Test
    void voteBug() {
        voteController.voteBug(1L, 10L, VoteType.UPVOTE);
        verify(voteService, times(1)).voteBug(1L, 10L, VoteType.UPVOTE);
    }

    @Test
    void voteComment() {
        voteController.voteComment(1L, 100L, VoteType.DOWNVOTE);
        verify(voteService, times(1)).voteComment(1L, 100L, VoteType.DOWNVOTE);
    }

    @Test
    void getBugVoteCount() {
        when(voteService.getBugVoteCount(10L)).thenReturn(5);

        Object result = voteController.getBugVoteCount(10L);

        assertEquals(5, result);
        verify(voteService, times(1)).getBugVoteCount(10L);
    }

    @Test
    void getCommentVoteCount() {
        when(voteService.getCommentVoteCount(100L)).thenReturn(-2);

        Object result = voteController.getCommentVoteCount(100L);

        assertEquals(-2, result);
        verify(voteService, times(1)).getCommentVoteCount(100L);
    }
}