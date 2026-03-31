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

    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BugRepository bugRepository;
    @Autowired
    private CommentRepository commentRepository;

    public List<Vote> findAll() {
       return voteRepository.findAll();
    }

    @Transactional
    public void voteBug(Long userId, Long bugId, VoteType type) {

        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        Bug bug = bugRepository.findById(bugId).
                orElseThrow(() -> new RuntimeException("Bug not found"));

        if(bug.getAuthor().equals(user)) {
            throw  new RuntimeException("You can't vote your bug");
        }

        if(voteRepository.existsByUserAndBug(user, bug)){
            throw new  RuntimeException("User already voted on this bug");
        }

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setBug(bug);
        vote.setType(type);
        voteRepository.save(vote);
    }

    public void voteComment(Long userId, Long commentId, VoteType type) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = commentRepository.findById(commentId).
                orElseThrow(() -> new RuntimeException("Comment not found"));

        if(comment.getAuthor().equals(user)) {
            throw  new RuntimeException("You can't vote your comment");
        }

        if(voteRepository.existsByUserAndComment(user, comment)){
            throw new  RuntimeException("User already voted on this comment");
        }

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setComment(comment);
        vote.setType(type);
        voteRepository.save(vote);
    }

    public Integer getBugVoteCount(Long bugId) {
        return voteRepository.getBugVoteCount(bugId);
    }

    public Integer getCommentVoteCount(Long commentId) {
        return voteRepository.getCommentVoteCount(commentId);
    }
}