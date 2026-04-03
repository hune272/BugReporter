package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.enums.BugStatus;
import com.bug_reporter.backend.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bugs")
@CrossOrigin
public class BugController {

    @Autowired
    private BugService bugService;

    @GetMapping
    public List<Bug> getBugs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long tagId) {

        return bugService.getFilteredBugs(title, authorId, tagId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bug> getBugById(@PathVariable("id") Long id) {
        Bug bug = bugService.findById(id);
        return bug != null ? ResponseEntity.ok(bug) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createBug(@RequestBody Map<String, Object> body) {
        try {
            Bug bug = bugService.save(
                    (String) body.get("title"),
                    (String) body.get("text"),
                    (String) body.get("picture"),
                    getLongValue(body.get("authorId")),
                    getLongListValue(body.get("tagIds"))
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(bug);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBug(@PathVariable Long id,
                                       @RequestBody Map<String, Object> body) {
        try {
            Bug bug = bugService.updateBug(
                    id,
                    (String) body.get("title"),
                    (String) body.get("text"),
                    (String) body.get("picture"),
                    getBugStatus(body.get("status")),
                    getLongListValue(body.get("tagIds"))
            );
            return ResponseEntity.ok(bug);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBug(@PathVariable Long id) {
        try {
            bugService.deleteBug(id);
            return ResponseEntity.ok(Map.of("message", "Bug deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Long getLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }

    private List<Long> getLongListValue(Object value) {
        if (value == null) {
            return null;
        }
        return ((List<?>) value).stream()
                .map(this::getLongValue)
                .toList();
    }

    private BugStatus getBugStatus(Object value) {
        if (value == null) {
            return null;
        }
        return BugStatus.valueOf(value.toString());
    }

}
