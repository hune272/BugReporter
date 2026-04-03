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

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BugRepository bugRepository;

    public List<Comment> findAll() {
        return (List<Comment>) commentRepository.findAll();
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment save(String commentText, String imageUrl, Long authorId, Long bugId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new RuntimeException("Bug not found"));

        Comment comment = new Comment();
        comment.setComment(commentText);
        comment.setImageUrl(imageUrl);
        comment.setAuthor(author);
        comment.setBug(bug);

        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, String commentText, String imageUrl, Long authorId, Long bugId) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        existingComment.setComment(commentText);
        existingComment.setImageUrl(imageUrl);

        if (authorId != null) {
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            existingComment.setAuthor(author);
        }

        if (bugId != null) {
            Bug bug = bugRepository.findById(bugId)
                    .orElseThrow(() -> new RuntimeException("Bug not found"));
            existingComment.setBug(bug);
        }

        return commentRepository.save(existingComment);
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public List<Comment> getCommentsByBugId(Long bugId) {
        return commentRepository.findByBugIdOrderByCreatedAtAsc(bugId);
    }
}
