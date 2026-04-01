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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    private Vote testVote;

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

        testVote = new Vote();
        testVote.setId(1000L);
        testVote.setType(VoteType.UPVOTE);
        testVote.setUser(testUser);
        testVote.setBug(testBug);
    }

    @Test
    void findAll() {
        when(voteRepository.findAll()).thenReturn(List.of(testVote));

        List<Vote> result = voteService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VoteType.UPVOTE, result.get(0).getType());

        verify(voteRepository, times(1)).findAll();
    }

    @Test
    void voteBug() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bugRepository.findById(10L)).thenReturn(Optional.of(testBug));

        voteService.voteBug(1L, 10L, VoteType.UPVOTE);
        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    void voteComment() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));

        voteService.voteComment(1L, 100L, VoteType.DOWNVOTE);
        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    void getBugVoteCount() {
        when(voteRepository.getBugVoteCount(10L)).thenReturn(1);
        Object result = voteService.getBugVoteCount(10L);

        Integer count = (Integer) result;

        assertEquals(1, count);
        verify(voteRepository, times(1)).getBugVoteCount(10L);
    }

    @Test
    void getCommentVoteCount() {
        Vote commentVote = new Vote();
        commentVote.setType(VoteType.UPVOTE);
        commentVote.setComment(testComment);

        lenient().when(voteRepository.findByCommentId(100L)).thenReturn(List.of(commentVote));

        Object count = voteService.getCommentVoteCount(100L);

        assertNotNull(count);
        verify(voteRepository, times(1)).getCommentVoteCount(100L);
    }
}