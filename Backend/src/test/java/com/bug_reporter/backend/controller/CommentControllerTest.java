package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.service.CommentService;
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
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(1L);
        comment.setComment("Initial comment");
        comment.setImageUrl("image.png");
    }

    @Test
    void getAllComments() {
        when(commentService.findAll()).thenReturn(List.of(comment));

        List<Comment> result = commentController.getAllComments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(comment, result.getFirst());
    }

    @Test
    void getCommentById() {
        when(commentService.findById(1L)).thenReturn(comment);

        Comment result = commentController.getCommentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCommentsByBugId() {
        when(commentService.getCommentsByBugId(2L)).thenReturn(List.of(comment));

        List<Comment> result = commentController.getCommentsByBugId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(comment, result.getFirst());
    }

    @Test
    void addComment() {
        when(commentService.save(comment)).thenReturn(comment);

        Comment result = commentController.addComment(comment);

        assertNotNull(result);
        assertEquals(comment, result);
        verify(commentService, times(1)).save(comment);
    }

    @Test
    void updateComment() {
        Comment updatedComment = new Comment();
        updatedComment.setComment("Updated");
        updatedComment.setImageUrl("updated.png");

        when(commentService.findById(1L)).thenReturn(comment);
        when(commentService.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentController.updateComment(1L, updatedComment);

        assertNotNull(result);
        assertEquals("Updated", result.getComment());
        assertEquals("updated.png", result.getImageUrl());
        verify(commentService, times(1)).save(comment);
    }

    @Test
    void deleteComment() {
        when(commentService.findById(1L)).thenReturn(comment);

        String result = commentController.deleteComment(1L);

        assertEquals("Comment deleted successfully", result);
        verify(commentService, times(1)).delete(comment);
    }
}