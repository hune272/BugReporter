package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@CrossOrigin
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public List<Tag> getAllTags() {
        return tagService.findAll();
    }

    @GetMapping("/{id}")
    public Tag getTagById(@PathVariable Long id) {
        return tagService.findById(id);
    }

    @GetMapping("/search")
    public Tag getTagByName(@RequestParam String name) {
        return tagService.findByName(name);
    }

    @PostMapping
    public Tag addTag(@RequestBody Tag tag) {
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            return null;
        }

        if (tagService.existsByNameIgnoreCase(tag.getName())) {
            return tagService.findByName(tag.getName());
        }

        tag.setName(tag.getName().trim());
        return tagService.save(tag);
    }

    @PutMapping("/{id}")
    public Tag updateTag(@PathVariable Long id, @RequestBody Tag updatedTag) {
        Tag existingTag = tagService.findById(id);

        if (existingTag == null) {
            return null;
        }

        if (updatedTag.getName() == null || updatedTag.getName().trim().isEmpty()) {
            return null;
        }

        existingTag.setName(updatedTag.getName().trim());
        return tagService.save(existingTag);
    }

    @DeleteMapping("/{id}")
    public String deleteTag(@PathVariable Long id) {
        Tag existingTag = tagService.findById(id);

        if (existingTag == null) {
            return "Tag not found";
        }

        tagService.delete(existingTag);
        return "Tag deleted successfully";
    }
}