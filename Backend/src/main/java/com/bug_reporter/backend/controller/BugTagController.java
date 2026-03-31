package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.service.BugTagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bugs/{bugId}/tags")
@CrossOrigin
public class BugTagController {

    private final BugTagService bugTagService;

    public BugTagController(BugTagService bugTagService) {
        this.bugTagService = bugTagService;
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getTagsByBugId(@PathVariable Long bugId) {
        List<Tag> tags = bugTagService.getTagsByBugId(bugId);
        return ResponseEntity.ok(tags);
    }

    @PostMapping("/{tagId}")
    public ResponseEntity<?> addTagToBug(@PathVariable Long bugId, @PathVariable Long tagId) {
        try {
            bugTagService.addTagToBug(bugId, tagId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<?> removeTagFromBug(@PathVariable Long bugId, @PathVariable Long tagId) {
        try {
            bugTagService.removeTagFromBug(bugId, tagId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> removeAllTagsFromBug(@PathVariable Long bugId) {
        try {
            bugTagService.removeAllTagsFromBug(bugId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}