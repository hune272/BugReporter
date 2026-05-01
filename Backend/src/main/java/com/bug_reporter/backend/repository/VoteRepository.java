package com.bug_reporter.backend.repository;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByUserAndBug(User user, Bug bug);

    boolean existsByUserAndComment(User user, Comment comment);

    List<Vote> findByBugId(Long bugId);

    List<Vote> findByCommentId(Long commentId);
}