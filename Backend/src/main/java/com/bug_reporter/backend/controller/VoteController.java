package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping
    public ResponseEntity<List<Vote>> findAll() {
        return ResponseEntity.ok(voteService.findAll());
        //200 OK
    }

    @PostMapping("/bug")
    public ResponseEntity<?> voteBug(@RequestBody Vote vote) {
        try {
            Vote created = voteService.voteBug(vote);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            //201 Created
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            //403 Forbidden
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
            //409 Conflict
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            //404 Not Found
        }
    }

    @PostMapping("/comment")
    public ResponseEntity<?> voteComment(@RequestBody Vote vote) {
        try {
            Vote created = voteService.voteComment(vote);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            //201 Created
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            //403 Forbidden
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
            //409 Conflict
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            //404 Not Found
        }
    }

    @GetMapping("/bug/{bugId}/count")
    public ResponseEntity<?> getBugVoteCount(@PathVariable Long bugId) {
        try {
            return ResponseEntity.ok(voteService.getBugVoteCount(bugId));
            //200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            //404 Not Found
        }
    }

    @GetMapping("/comment/{commentId}/count")
    public ResponseEntity<?> getCommentVoteCount(@PathVariable Long commentId) {
        try {
            return ResponseEntity.ok(voteService.getCommentVoteCount(commentId));
            //200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            //404 Not Found
        }
    }
}
