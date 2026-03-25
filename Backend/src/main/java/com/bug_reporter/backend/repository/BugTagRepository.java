package com.bug_reporter.backend.repository;

import com.bug_reporter.backend.model.BugTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugTagRepository extends JpaRepository<BugTag, Long> {
}