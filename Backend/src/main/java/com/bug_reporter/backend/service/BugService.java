package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.BugTag;
import com.bug_reporter.backend.model.Tag;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.enums.BugStatus;
import com.bug_reporter.backend.repository.BugRepository;
import com.bug_reporter.backend.repository.TagRepository;
import com.bug_reporter.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BugService {

    @Autowired
    private BugRepository bugRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;

    public List<Bug> findAll() {
        return bugRepository.findAll();
    }

    public Bug findById(Long id) {
        return bugRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Bug bug, Long authorId, List<Long> tagId) {

        User author = userRepository.findById(authorId).
                        orElseThrow(() -> new RuntimeException("User not found"));
        bug.setAuthor(author);
        bug.setCreatedAt(LocalDateTime.now());
        bug.setStatus(BugStatus.RECEIVED);

        List<BugTag> bugTags = new ArrayList<>();
        if(tagId != null && !tagId.isEmpty()) {
            for(Long tagId1 : tagId) {
                Tag tag =  tagRepository.findById(tagId1).
                        orElseThrow(() -> new RuntimeException("Tag not found"));

                BugTag bugTag = new BugTag();
                bugTag.setBug(bug);
                bugTag.setTag(tag);
                bugTags.add(bugTag);
            }
        }
        bug.setBugTags(bugTags);
        bugRepository.save(bug);
    }

    public List<Bug> getAllBugsByAuthorId(Long authorId) {
        return bugRepository.getAllBugsByAuthorId(authorId);
    }

    public List<Bug> getAllBugsByTitle(String title) {
        return bugRepository.getAllBugsByTitle(title);
    }

    public void delete(Bug op) {
        bugRepository.delete(op);
    }

    public List<Bug> getFilteredBugs(String title, Long authorId, Long tagId) {
        Sort sortByDataDesc = Sort.by(Sort.Direction.DESC, "createdAt");

        if(authorId != null) {
            return bugRepository.findByAuthorId(authorId, sortByDataDesc);
        }
        if(title != null && !title.isEmpty()) {
            return bugRepository.findByTitleContainingIgnoreCase(title, sortByDataDesc);
        }
        if(tagId != null) {
            return bugRepository.findByBugTags_Tag_Id(tagId, sortByDataDesc);
        }

        return bugRepository.findAll(sortByDataDesc);
    }

    @Transactional
    public Bug updateBug(Long id, Bug updatedBugData, Long requesterId) {
        Bug bug = bugRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Bug not found"));

        if(!bug.getAuthor().getId().equals(requesterId)) {
            throw new RuntimeException("You are not allowed to update this bug");
        }

        bug.setTitle(updatedBugData.getTitle());
        bug.setText(updatedBugData.getText());
        bug.setPicture(updatedBugData.getPicture());
        bug.setComments(updatedBugData.getComments());
        bug.setBugTags(updatedBugData.getBugTags());
        return bugRepository.save(bug);
    }

    @Transactional
    public void deleteBug(Long id, Long requesterId) {
        Bug bug = bugRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bug not found"));
        if(!bug.getAuthor().getId().equals(requesterId)) {
            throw new RuntimeException("You are not allowed to delete this bug");
        }
        bugRepository.delete(bug);
    }
}