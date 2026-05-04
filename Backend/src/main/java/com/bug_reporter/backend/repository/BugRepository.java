package com.bug_reporter.backend.repository;

import com.bug_reporter.backend.model.Bug;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long>, JpaSpecificationExecutor<Bug> {

    @Override
    @NullMarked
    @EntityGraph(attributePaths = {"author", "bugTags", "bugTags.tag"})
    Optional<Bug> findById(Long id);

    @Override
    @NullMarked
    @EntityGraph(attributePaths = {"author"})
    Page<Bug> findAll(Specification<Bug> spec, Pageable pageable);
}
