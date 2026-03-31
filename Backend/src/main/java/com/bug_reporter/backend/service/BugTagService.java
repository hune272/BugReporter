package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.BugTag;
import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.repository.BugRepository;
import com.bug_reporter.backend.repository.BugTagRepository;
import com.bug_reporter.backend.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BugTagService {

    private final BugTagRepository bugTagRepository;
    private final BugRepository bugRepository;
    private final TagRepository tagRepository;

    public BugTagService(BugTagRepository bugTagRepository, BugRepository bugRepository, TagRepository tagRepository) {
        this.bugTagRepository = bugTagRepository;
        this.bugRepository = bugRepository;
        this.tagRepository = tagRepository;
    }

    public List<Tag> getTagsByBugId(Long bugId) {
        return bugTagRepository.findByBugId(bugId)
                .stream()
                .map(BugTag::getTag)
                .toList();
    }

    @Transactional
    public BugTag addTagToBug(Long bugId, Long tagId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + bugId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

        if (bugTagRepository.findByBugAndTag(bug, tag).isPresent()) {
            throw new RuntimeException("Tag '" + tag.getName() + "' is already assigned to this bug");
        }

        BugTag bugTag = new BugTag();
        bugTag.setBug(bug);
        bugTag.setTag(tag);
        return bugTagRepository.save(bugTag);
    }

    @Transactional
    public void removeTagFromBug(Long bugId, Long tagId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + bugId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

        BugTag bugTag = bugTagRepository.findByBugAndTag(bug, tag)
                .orElseThrow(() -> new RuntimeException("Tag '" + tag.getName() + "' is not assigned to this bug"));

        bugTagRepository.delete(bugTag);
    }

    @Transactional
    public void removeAllTagsFromBug(Long bugId) {
        if (!bugRepository.existsById(bugId)) {
            throw new RuntimeException("Bug not found with id: " + bugId);
        }
        bugTagRepository.deleteByBugId(bugId);
    }
}
