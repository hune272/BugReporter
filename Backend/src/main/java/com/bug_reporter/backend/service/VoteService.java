package com.bug_reporter.backend.service;

import com.bug_reporter.backend.dto.mapper.VoteMapper;
import com.bug_reporter.backend.dto.request.VoteBugRequest;
import com.bug_reporter.backend.dto.request.VoteCommentRequest;
import com.bug_reporter.backend.dto.response.VoteResponse;
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
    public VoteService(VoteRepository voteRepository, UserRepository userRepository, BugRepository bugRepository, CommentRepository commentRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.bugRepository = bugRepository;
        this.commentRepository = commentRepository;
    }

    public List<VoteResponse> findAllVotes() {
        return voteRepository.findAll().stream().map(VoteMapper::toResponse).toList();
    }

    @Transactional
    public VoteResponse voteBug(VoteBugRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Bug bug = bugRepository.findById(request.bugId()).orElseThrow(() -> new RuntimeException("Bug not found with id: " + request.bugId()));

        if (bug.getAuthor().equals(user)) {
            throw new SecurityException("You can't vote your own bug");
        }
        if (voteRepository.existsByUserAndBug(user, bug)) {
            throw new IllegalStateException("User already voted on this bug");
        }

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setBug(bug);
        vote.setType(request.type());
        return VoteMapper.toResponse(voteRepository.save(vote));
    }

    @Transactional
    public VoteResponse voteComment(VoteCommentRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Comment comment = commentRepository.findById(request.commentId()).orElseThrow(() -> new RuntimeException("Comment not found with id: " + request.commentId()));

        if (comment.getAuthor().equals(user)) {
            throw new SecurityException("You can't vote your own comment");
        }
        if (voteRepository.existsByUserAndComment(user, comment)) {
            throw new IllegalStateException("User already voted on this comment");
        }

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setComment(comment);
        vote.setType(request.type());
        return VoteMapper.toResponse(voteRepository.save(vote));
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
        return votes.stream().mapToInt(vote -> vote.getType() == VoteType.UPVOTE ? 1 : -1).sum();
    }
}
