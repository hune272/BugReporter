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
import java.util.Objects;
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
        bug.setBugTags(buildBugTags(bug, tagId));
        bugRepository.save(bug);
    }

    @Transactional
    public Bug save(String title, String text, String picture, Long authorId, List<Long> tagIds) {
        Bug bug = new Bug();
        bug.setTitle(title);
        bug.setText(text);
        bug.setPicture(picture);
        save(bug, authorId, tagIds);
        return bug;
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
        return bugRepository.findAll(sortByDataDesc).stream()
                .filter(bug -> authorId == null || Objects.equals(bug.getAuthor().getId(), authorId))
                .filter(bug -> title == null || title.isBlank() ||
                        (bug.getTitle() != null && bug.getTitle().toLowerCase().contains(title.toLowerCase())))
                .filter(bug -> tagId == null || (bug.getBugTags() != null &&
                        bug.getBugTags().stream().anyMatch(bugTag -> Objects.equals(bugTag.getTag().getId(), tagId))))
                .toList();
    }

    @Transactional
    public Bug updateBug(Long id, Bug updatedBugData) {
        Bug bug = bugRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Bug not found"));

        bug.setTitle(updatedBugData.getTitle());
        bug.setText(updatedBugData.getText());
        bug.setPicture(updatedBugData.getPicture());
        if (updatedBugData.getStatus() != null) {
            bug.setStatus(updatedBugData.getStatus());
        }
        return bugRepository.save(bug);
    }

    @Transactional
    public Bug updateBug(Long id,
                         String title,
                         String text,
                         String picture,
                         BugStatus status,
                         List<Long> tagIds) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found"));

        bug.setTitle(title);
        bug.setText(text);
        bug.setPicture(picture);
        if (status != null) {
            bug.setStatus(status);
        }
        if (tagIds != null) {
            if (bug.getBugTags() == null) {
                bug.setBugTags(new ArrayList<>());
            } else {
                bug.getBugTags().clear();
            }
            bug.getBugTags().addAll(buildBugTags(bug, tagIds));
        }

        return bugRepository.save(bug);
    }

    @Transactional
    public void deleteBug(Long id) {
        Bug bug = bugRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bug not found"));
        bugRepository.delete(bug);
    }

    private List<BugTag> buildBugTags(Bug bug, List<Long> tagIds) {
        List<BugTag> bugTags = new ArrayList<>();
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new RuntimeException("Tag not found"));

                BugTag bugTag = new BugTag();
                bugTag.setBug(bug);
                bugTag.setTag(tag);
                bugTags.add(bugTag);
            }
        }
        return bugTags;
    }
}
