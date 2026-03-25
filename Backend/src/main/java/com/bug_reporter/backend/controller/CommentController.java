package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.findAll();
    }

    @GetMapping("/{id}")
    public Comment getCommentById(@PathVariable Long id) {
        return commentService.findById(id);
    }

    @GetMapping("/bug/{bugId}")
    public List<Comment> getCommentsByBugId(@PathVariable Long bugId) {
        return commentService.getCommentsByBugId(bugId);
    }

    @PostMapping
    public Comment addComment(@RequestBody Comment comment) {
        return commentService.save(comment);
    }

    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
        Comment existingComment = commentService.findById(id);

        if (existingComment == null) {
            return null;
        }

        existingComment.setComment(updatedComment.getComment());
        existingComment.setImageUrl(updatedComment.getImageUrl());

        if (updatedComment.getAuthor() != null) {
            existingComment.setAuthor(updatedComment.getAuthor());
        }

        if (updatedComment.getBug() != null) {
            existingComment.setBug(updatedComment.getBug());
        }

        return commentService.save(existingComment);
    }

    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable Long id) {
        Comment existingComment = commentService.findById(id);

        if (existingComment == null) {
            return "Comment not found";
        }

        commentService.delete(existingComment);
        return "Comment deleted successfully";
    }
}