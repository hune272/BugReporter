package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.service.CommentService;
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
        ResponseEntity<List<Comment>> result = commentController.getAllComments();
        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getCommentById() {
        when(commentService.findById(1L)).thenReturn(comment);
        ResponseEntity<?> result = commentController.getCommentById(1L);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void getCommentById_notFound() {
        when(commentService.findById(99L)).thenThrow(new RuntimeException("Not found"));
        ResponseEntity<?> result = commentController.getCommentById(99L);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void getCommentsByBugId() {
        when(commentService.getCommentsByBugId(2L)).thenReturn(List.of(comment));
        ResponseEntity<List<Comment>> result = commentController.getCommentsByBugId(2L);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void addComment() {
        when(commentService.save(comment)).thenReturn(comment);
        ResponseEntity<?> result = commentController.addComment(comment);
        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    void updateComment() {
        Comment updated = new Comment();
        updated.setComment("Updated");
        when(commentService.updateComment(eq(1L), any(Comment.class))).thenReturn(updated);
        ResponseEntity<?> result = commentController.updateComment(1L, updated);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void deleteComment() {
        doNothing().when(commentService).deleteComment(1L);
        ResponseEntity<?> result = commentController.deleteComment(1L);
        assertEquals(204, result.getStatusCode().value());
    }

    @Test
    void deleteComment_notFound() {
        doThrow(new RuntimeException("Not found")).when(commentService).deleteComment(99L);
        ResponseEntity<?> result = commentController.deleteComment(99L);
        assertEquals(404, result.getStatusCode().value());
    }
}