package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> findAll() {
        return (List<Comment>) commentRepository.findAll();
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public List<Comment> getCommentsByBugId(Long bugId) {
        return commentRepository.findByBugIdOrderByCreatedAtAsc(bugId);
    }
}