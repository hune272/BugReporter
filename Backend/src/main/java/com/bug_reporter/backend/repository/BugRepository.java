package com.bug_reporter.backend.repository;

import com.bug_reporter.backend.model.Bug;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    List<Bug> getAllBugsByAuthorId(Long authorId);

    List<Bug> getAllBugsByTitle(String title);

    List<Bug> findByAuthorId(Long authorId, Sort sort);

    List<Bug> findByTitleContainingIgnoreCase(String title, Sort sort);

    List<Bug> findByBugTags_Tag_Id(Long tagId, Sort sort);

    List<Bug> findDistinctByTitleContainingIgnoreCaseAndAuthorId(String title, Long authorId, Sort sort);

    List<Bug> findDistinctByTitleContainingIgnoreCaseAndBugTags_Tag_Id(String title, Long tagId, Sort sort);

    List<Bug> findDistinctByAuthorIdAndBugTags_Tag_Id(Long authorId, Long tagId, Sort sort);

    List<Bug> findDistinctByTitleContainingIgnoreCaseAndAuthorIdAndBugTags_Tag_Id(String title, Long authorId, Long tagId, Sort sort);
}