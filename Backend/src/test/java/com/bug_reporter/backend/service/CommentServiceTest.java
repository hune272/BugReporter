package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.repository.CommentRepository;
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
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(1L);
        comment.setComment("First comment");
    }

    @Test
    void findAll() {
        when(commentRepository.findAll()).thenReturn(List.of(comment));
        List<Comment> result = commentService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(comment, result.getFirst());
    }

    @Test
    void findById() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        Comment result = commentService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_notFound() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.findById(99L));
    }

    @Test
    void save() {
        when(commentRepository.save(comment)).thenReturn(comment);
        Comment result = commentService.save(comment);
        assertNotNull(result);
        assertEquals("First comment", result.getComment());
    }

    @Test
    void updateComment() {
        Comment updatedComment = new Comment();
        updatedComment.setComment("Updated");
        updatedComment.setImageUrl("new.png");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.updateComment(1L, updatedComment);
        assertNotNull(result);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void deleteComment() {
        when(commentRepository.existsById(1L)).thenReturn(true);
        commentService.deleteComment(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteComment_notFound() {
        when(commentRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> commentService.deleteComment(99L));
    }

    @Test
    void getCommentsByBugId() {
        when(commentRepository.findByBugIdOrderByCreatedAtAsc(2L)).thenReturn(List.of(comment));
        List<Comment> result = commentService.getCommentsByBugId(2L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
