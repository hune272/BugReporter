package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.model.enums.VoteType;
import com.bug_reporter.backend.repository.BugRepository;
import com.bug_reporter.backend.repository.CommentRepository;
import com.bug_reporter.backend.repository.UserRepository;
import com.bug_reporter.backend.repository.VoteRepository;
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
class VoteServiceTest {

    @Mock
    private BugRepository bugRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private VoteService voteService;

    private User testUser;
    private User authorUser;
    private Bug testBug;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("Votant");

        authorUser = new User();
        authorUser.setId(2L);
        authorUser.setUsername("Autor");

        testBug = new Bug();
        testBug.setId(10L);
        testBug.setTitle("Bug pentru vot");
        testBug.setAuthor(authorUser);

        testComment = new Comment();
        testComment.setId(100L);
        testComment.setComment("Comentariu util");
        testComment.setAuthor(authorUser);
    }

    @Test
    void findAll() {
        Vote testVote = new Vote();
        testVote.setType(VoteType.UPVOTE);
        when(voteRepository.findAll()).thenReturn(List.of(testVote));

        List<Vote> result = voteService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VoteType.UPVOTE, result.get(0).getType());
    }

    @Test
    void voteBug() {
        Vote vote = new Vote();
        User user = new User();
        user.setId(1L);
        Bug bug = new Bug();
        bug.setId(10L);
        vote.setUser(user);
        vote.setBug(bug);
        vote.setType(VoteType.UPVOTE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bugRepository.findById(10L)).thenReturn(Optional.of(testBug));

        voteService.voteBug(vote);
        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    void voteComment() {
        Vote vote = new Vote();
        User user = new User();
        user.setId(1L);
        Comment comment = new Comment();
        comment.setId(100L);
        vote.setUser(user);
        vote.setComment(comment);
        vote.setType(VoteType.DOWNVOTE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));

        voteService.voteComment(vote);
        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    void getBugVoteCount() {
        when(bugRepository.existsById(10L)).thenReturn(true);
        when(voteRepository.getBugVoteCount(10L)).thenReturn(1);

        Integer count = voteService.getBugVoteCount(10L);

        assertEquals(1, count);
        verify(voteRepository, times(1)).getBugVoteCount(10L);
    }

    @Test
    void getCommentVoteCount() {
        when(commentRepository.existsById(100L)).thenReturn(true);
        when(voteRepository.getCommentVoteCount(100L)).thenReturn(3);

        Integer count = voteService.getCommentVoteCount(100L);

        assertEquals(3, count);
        verify(voteRepository, times(1)).getCommentVoteCount(100L);
    }
}