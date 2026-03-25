package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public List<Tag> findAll() {
        return (List<Tag>) tagRepository.findAll();
    }

    public Tag findById(Long id) {
        return tagRepository.findById(id).orElse(null);
    }

    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    public void delete(Tag tag) {
        tagRepository.delete(tag);
    }

    public Tag findByName(String name){
        return tagRepository.findByNameIgnoreCase(name).orElse(null);
    }

    public boolean existsByNameIgnoreCase(String name){
        return tagRepository.existsByNameIgnoreCase(name);
    }
}