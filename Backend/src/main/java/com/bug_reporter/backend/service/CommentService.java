package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.repository.BugRepository;
import com.bug_reporter.backend.repository.CommentRepository;
import com.bug_reporter.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BugRepository bugRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, BugRepository bugRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.bugRepository = bugRepository;
        this.userRepository = userRepository;
    }

    public List<Comment> findAll() {
        return (List<Comment>) commentRepository.findAll();
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
    }

    public Comment save(Comment comment) {
        if (comment.getBug() != null && comment.getBug().getId() != null) {
            Bug bug = bugRepository.findById(comment.getBug().getId())
                    .orElseThrow(() -> new RuntimeException("Bug not found with id: " + comment.getBug().getId()));
            comment.setBug(bug);
        }
        if (comment.getAuthor() != null && comment.getAuthor().getId() != null) {
            User author = userRepository.findById(comment.getAuthor().getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + comment.getAuthor().getId()));
            comment.setAuthor(author);
        }
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, Comment updatedComment) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        existingComment.setComment(updatedComment.getComment());
        existingComment.setImageUrl(updatedComment.getImageUrl());

        if (updatedComment.getAuthor() != null) {
            existingComment.setAuthor(updatedComment.getAuthor());
        }
        if (updatedComment.getBug() != null) {
            existingComment.setBug(updatedComment.getBug());
        }
        return commentRepository.save(existingComment);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    public List<Comment> getCommentsByBugId(Long bugId) {
        return commentRepository.findByBugIdOrderByCreatedAtAsc(bugId);
    }
}