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
        verify(commentRepository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Comment result = commentService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    void save() {
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = commentService.save(comment);

        assertNotNull(result);
        assertEquals("First comment", result.getComment());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void delete() {
        commentService.delete(comment);

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void getCommentsByBugId() {
        when(commentRepository.findByBugIdOrderByCreatedAtAsc(2L)).thenReturn(List.of(comment));

        List<Comment> result = commentService.getCommentsByBugId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(comment, result.getFirst());
        verify(commentRepository, times(1)).findByBugIdOrderByCreatedAtAsc(2L);
    }
}
