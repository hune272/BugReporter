package com.bug_reporter.backend.service;

import com.bug_reporter.backend.dto.request.TagCreateRequest;
import com.bug_reporter.backend.dto.response.TagSummary;
import com.bug_reporter.backend.dto.mapper.TagMapper;
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

    public List<TagSummary> findAllTags() {
        return ((List<Tag>) tagRepository.findAll()).stream()
                .map(TagMapper::toSummary)
                .toList();
    }

    public TagSummary findTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
        return TagMapper.toSummary(tag);
    }

    public TagSummary createTag(TagCreateRequest request) {
        Tag tag = TagMapper.toEntity(request);

        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name is required");
        }
        if (tagRepository.existsByNameIgnoreCase(tag.getName())) {
            throw new IllegalStateException("Tag with name '" + tag.getName() + "' already exists");
        }

        tag.setName(tag.getName().trim());
        return TagMapper.toSummary(tagRepository.save(tag));
    }

    public TagSummary updateTag(Long id, TagCreateRequest request) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
        Tag updatedTag = TagMapper.toEntity(request);

        if (updatedTag.getName() == null || updatedTag.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name is required");
        }

        existingTag.setName(updatedTag.getName().trim());
        return TagMapper.toSummary(tagRepository.save(existingTag));
    }

    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
        tagRepository.delete(tag);
    }

    public TagSummary findTagByName(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .map(TagMapper::toSummary)
                .orElse(null);
    }

}
