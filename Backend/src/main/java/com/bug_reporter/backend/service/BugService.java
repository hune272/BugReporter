package com.bug_reporter.backend.service;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.enums.BugStatus;
import com.bug_reporter.backend.repository.BugRepository;
import com.bug_reporter.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BugService {

    private final BugRepository bugRepository;
    private final UserRepository userRepository;

    @Autowired
    public BugService(BugRepository bugRepository, UserRepository userRepository) {
        this.bugRepository = bugRepository;
        this.userRepository = userRepository;
    }

    public List<Bug> findAll() {
        return bugRepository.findAll();
    }

    public Bug findById(Long id) {
        return bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));
    }

    @Transactional
    public Bug save(Bug bug) {
        if (bug.getAuthor() == null || bug.getAuthor().getId() == null) {
            throw new IllegalArgumentException("Author is required");
        }

        User author = userRepository.findById(bug.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + bug.getAuthor().getId()));
        bug.setAuthor(author);
        bug.setCreatedAt(LocalDateTime.now());
        bug.setStatus(BugStatus.RECEIVED);

        return bugRepository.save(bug);
    }

    public List<Bug> getFilteredBugs(String title, Long authorId, Long tagId) {
        Sort sortByDataDesc = Sort.by(Sort.Direction.DESC, "createdAt");

        if (authorId != null) {
            return bugRepository.findByAuthorId(authorId, sortByDataDesc);
        }
        if (title != null && !title.isEmpty()) {
            return bugRepository.findByTitleContainingIgnoreCase(title, sortByDataDesc);
        }
        if (tagId != null) {
            return bugRepository.findByBugTags_Tag_Id(tagId, sortByDataDesc);
        }
        return bugRepository.findAll(sortByDataDesc);
    }

    @Transactional
    public Bug updateBug(Long id, Bug updatedBugData, Long requesterId) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));

        if (!bug.getAuthor().getId().equals(requesterId)) {
            throw new SecurityException("You are not allowed to update this bug");
        }

        bug.setTitle(updatedBugData.getTitle());
        bug.setText(updatedBugData.getText());
        bug.setPicture(updatedBugData.getPicture());
        return bugRepository.save(bug);
    }

    @Transactional
    public void deleteBug(Long id, Long requesterId) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));
        if (!bug.getAuthor().getId().equals(requesterId)) {
            throw new SecurityException("You are not allowed to delete this bug");
        }
        bugRepository.delete(bug);
    }
}