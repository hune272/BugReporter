package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.model.enums.VoteType;
import com.bug_reporter.backend.repository.VoteRepository;
import com.bug_reporter.backend.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin
public class VoteController {
    @Autowired
    private VoteService voteService;

    @GetMapping
    public List<Vote> findAll() {
        return voteService.findAll();
    }

    @PostMapping("/bug")
    public void voteBug(@RequestParam Long userId,
                        @RequestParam Long bugId,
                        @RequestParam VoteType type) {
        voteService.voteBug(userId, bugId, type);
    }

    @PostMapping("/comment")
    public void voteComment(@RequestParam Long userId, @RequestParam Long commentId, @RequestParam VoteType type) {
        voteService.voteComment(userId, commentId, type);
    }

    @GetMapping("/bug/{bugId}/count")
    public Integer getBugVoteCount(@RequestParam Long bugId) {
        return voteService.getBugVoteCount(bugId);
    }

    @GetMapping("/comment/{commentId}/count")
    public Integer getCommentVoteCount(@RequestParam Long commentId) {
        return voteService.getCommentVoteCount(commentId);
    }

}