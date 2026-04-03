package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/comments"})
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
    public Comment addComment(@RequestBody Map<String, Object> body) {
        return commentService.save(
                (String) body.get("comment"),
                (String) body.get("imageUrl"),
                getLongValue(body.get("authorId")),
                getLongValue(body.get("bugId"))
        );
    }

    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return commentService.updateComment(
                id,
                (String) body.get("comment"),
                (String) body.get("imageUrl"),
                getLongValue(body.get("authorId")),
                getLongValue(body.get("bugId"))
        );
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

    private Long getLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }
}
