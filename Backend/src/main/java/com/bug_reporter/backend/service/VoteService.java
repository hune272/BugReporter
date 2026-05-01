package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.model.enums.VoteType;
import com.bug_reporter.backend.repository.BugRepository;
import com.bug_reporter.backend.repository.CommentRepository;
import com.bug_reporter.backend.repository.UserRepository;
import com.bug_reporter.backend.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final BugRepository bugRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository, UserRepository userRepository,
                       BugRepository bugRepository, CommentRepository commentRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.bugRepository = bugRepository;
        this.commentRepository = commentRepository;
    }

    public List<Vote> findAll() {
        return voteRepository.findAll();
    }

    @Transactional
    public Vote voteBug(Vote vote) {
        if (vote.getUser() == null || vote.getUser().getId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (vote.getBug() == null || vote.getBug().getId() == null) {
            throw new IllegalArgumentException("Bug ID is required");
        }

        User user = userRepository.findById(vote.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + vote.getUser().getId()));
        Bug bug = bugRepository.findById(vote.getBug().getId())
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + vote.getBug().getId()));

        if (bug.getAuthor().equals(user)) {
            throw new SecurityException("You can't vote your own bug");
        }
        if (voteRepository.existsByUserAndBug(user, bug)) {
            throw new IllegalStateException("User already voted on this bug");
        }

        vote.setUser(user);
        vote.setBug(bug);
        return voteRepository.save(vote);
    }

    @Transactional
    public Vote voteComment(Vote vote) {
        if (vote.getUser() == null || vote.getUser().getId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (vote.getComment() == null || vote.getComment().getId() == null) {
            throw new IllegalArgumentException("Comment ID is required");
        }

        User user = userRepository.findById(vote.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + vote.getUser().getId()));
        Comment comment = commentRepository.findById(vote.getComment().getId())
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + vote.getComment().getId()));

        if (comment.getAuthor().equals(user)) {
            throw new SecurityException("You can't vote your own comment");
        }
        if (voteRepository.existsByUserAndComment(user, comment)) {
            throw new IllegalStateException("User already voted on this comment");
        }

        vote.setUser(user);
        vote.setComment(comment);
        return voteRepository.save(vote);
    }

    public Integer getBugVoteCount(Long bugId) {
        if (!bugRepository.existsById(bugId)) {
            throw new RuntimeException("Bug not found with id: " + bugId);
        }
        return calculateVoteCount(voteRepository.findByBugId(bugId));
    }

    public Integer getCommentVoteCount(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found with id: " + commentId);
        }
        return calculateVoteCount(voteRepository.findByCommentId(commentId));
    }

    private int calculateVoteCount(List<Vote> votes) {
        return votes.stream()
                .mapToInt(vote -> vote.getType() == VoteType.UPVOTE ? 1 : -1)
                .sum();
    }
}
