package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bugs")
@CrossOrigin
public class BugController {

    private final BugService bugService;

    @Autowired
    public BugController(BugService bugService) {
        this.bugService = bugService;
    }

    @GetMapping
    public ResponseEntity<List<Bug>> getBugs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long tagId) {

        return ResponseEntity.ok(bugService.getFilteredBugs(title, authorId, tagId));
        //200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBugById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bugService.findById(id));
            //200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            //404 Not Found
        }
    }

    @PostMapping
    public ResponseEntity<?> createBug(@RequestBody Bug bug) {
        try {
            Bug created = bugService.save(bug);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            //201 Created
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            //400 Bad Request
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBug(@PathVariable Long id,
                                       @RequestBody Bug bug,
                                       @AuthenticationPrincipal Long requesterId) {
        if (requesterId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
            //401 Unauthorized
        }

        try {
            Bug updated = bugService.updateBug(id, bug, requesterId);
            return ResponseEntity.ok(updated);
            //200 OK
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            //403 Forbidden
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            //404 Not Found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBug(@PathVariable Long id,
                                       @AuthenticationPrincipal Long requesterId) {
        if (requesterId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
            //401 Unauthorized
        }

        try {
            bugService.deleteBug(id, requesterId);
            return ResponseEntity.noContent().build();
            //204 No Content
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            //403 Forbidden
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            //404 Not Found
        }
    }
}