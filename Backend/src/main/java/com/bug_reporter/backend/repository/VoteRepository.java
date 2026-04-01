package com.bug_reporter.backend.repository;

import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.model.enums.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT " +
            "SUM(CASE WHEN v.type = 'UPVOTE' THEN 1 ELSE 0 END) - " +
            "SUM(CASE WHEN v.type = 'DOWNVOTE' THEN 1 ELSE 0 END) " +
            "FROM Vote v WHERE v.bug.id = :bugId")
    Integer getBugVoteCount(Long bugId);

    @Query("SELECT " +
            "SUM(CASE WHEN v.type = 'UPVOTE' THEN 1 ELSE 0 END) - " +
            "SUM(CASE WHEN v.type = 'DOWNVOTE' THEN 1 ELSE 0 END) " +
            "FROM Vote v WHERE v.comment.id = :commentId")
    Integer getCommentVoteCount(Long commentId);

    boolean existsByUserAndBug(User user, Bug bug);

    boolean existsByUserAndComment(User user, Comment comment);

    List<Vote> findByBugId(Long bugId);

    List<Vote> findByCommentId(Long commentId);
}