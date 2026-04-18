package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/comments"})
@CrossOrigin
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.findAll());
        //200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(commentService.findById(id));
            //200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            //404 Not Found
        }
    }

    @GetMapping("/bug/{bugId}")
    public ResponseEntity<List<Comment>> getCommentsByBugId(@PathVariable Long bugId) {
        return ResponseEntity.ok(commentService.getCommentsByBugId(bugId));
        //200 OK
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Comment comment) {
        try {
            Comment created = commentService.save(comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            //201 Created
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            //400 Bad Request
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
        try {
            return ResponseEntity.ok(commentService.updateComment(id, updatedComment));
            //200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            //404 Not Found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
            //204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            //404 Not Found
        }
    }
}