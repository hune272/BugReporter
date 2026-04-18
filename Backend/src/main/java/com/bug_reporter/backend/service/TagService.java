package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> findAll() {
        return (List<Tag>) tagRepository.findAll();
    }

    public Tag findById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
    }

    public Tag createTag(Tag tag) {
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name is required");
        }

        if (existsByNameIgnoreCase(tag.getName())) {
            throw new IllegalStateException("Tag with name '" + tag.getName() + "' already exists");
        }

        tag.setName(tag.getName().trim());
        return tagRepository.save(tag);
    }

    public Tag updateTag(Long id, Tag updatedTag) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));

        if (updatedTag.getName() == null || updatedTag.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name is required");
        }

        existingTag.setName(updatedTag.getName().trim());
        return tagRepository.save(existingTag);
    }

    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
        tagRepository.delete(tag);
    }

    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    public Tag findByName(String name) {
        return tagRepository.findByNameIgnoreCase(name).orElse(null);
    }

    public boolean existsByNameIgnoreCase(String name) {
        return tagRepository.existsByNameIgnoreCase(name);
    }
}