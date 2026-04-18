package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.model.enums.VoteType;
import com.bug_reporter.backend.service.VoteService;
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
        ResponseEntity<List<Vote>> result = voteController.findAll();
        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void voteBug() {
        Vote inputVote = new Vote();
        User user = new User(); user.setId(1L);
        Bug bug = new Bug(); bug.setId(10L);
        inputVote.setUser(user); inputVote.setBug(bug); inputVote.setType(VoteType.UPVOTE);
        when(voteService.voteBug(any(Vote.class))).thenReturn(testVote);
        ResponseEntity<?> result = voteController.voteBug(inputVote);
        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    void voteComment() {
        Vote inputVote = new Vote();
        User user = new User(); user.setId(1L);
        Comment comment = new Comment(); comment.setId(100L);
        inputVote.setUser(user); inputVote.setComment(comment); inputVote.setType(VoteType.DOWNVOTE);
        when(voteService.voteComment(any(Vote.class))).thenReturn(testVote);
        ResponseEntity<?> result = voteController.voteComment(inputVote);
        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    void getBugVoteCount() {
        when(voteService.getBugVoteCount(10L)).thenReturn(5);
        ResponseEntity<?> result = voteController.getBugVoteCount(10L);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(5, result.getBody());
    }

    @Test
    void getCommentVoteCount() {
        when(voteService.getCommentVoteCount(100L)).thenReturn(-2);
        ResponseEntity<?> result = voteController.getCommentVoteCount(100L);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(-2, result.getBody());
    }
}
