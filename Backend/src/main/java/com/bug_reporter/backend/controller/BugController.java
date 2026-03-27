package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;

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
    public Bug getBugById(@PathVariable("id") Long id) {
        return bugService.findById(id);
    }

    @PostMapping
    public void createBug(@RequestBody Bug bug, @RequestParam Long authorId, @RequestParam List<Long> tagId) {
        bugService.save(bug, authorId, tagId);
    }

    @PutMapping("/{id}")
    public Bug updateBug(@PathVariable Long id,
                         @RequestBody Bug updatedBugData,
                         @RequestParam Long requesterId) {
        return bugService.updateBug(id, updatedBugData, requesterId);
    }

    @DeleteMapping("/{id}")
    public String deleteBug(@PathVariable Long id, @RequestParam Long requesterId) {
        bugService.deleteBug(id, requesterId);
        return "Bug deleted";
    }

}