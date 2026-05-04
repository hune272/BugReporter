package com.bug_reporter.backend.dto.mapper;

import com.bug_reporter.backend.dto.response.CommentResponse;
import com.bug_reporter.backend.model.Comment;
import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.model.enums.VoteType;

import java.util.List;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static CommentResponse toResponse(Comment comment) {
        return toResponse(comment, 0.0);
    }

    public static CommentResponse toResponse(Comment comment, double authorScore) {
        if (comment == null) return null;
        return new CommentResponse(
                comment.getId(),
                comment.getComment(),
                comment.getImageUrl(),
                comment.getCreatedAt(),
                UserMapper.toSummary(comment.getAuthor(), authorScore),
                comment.getBug() == null ? null : comment.getBug().getId(),
                computeVoteCount(comment.getVotes())
        );
    }

    private static int computeVoteCount(List<Vote> votes) {
        if (votes == null) return 0;
        return votes.stream()
                .mapToInt(vote -> vote.getType() == VoteType.UPVOTE ? 1 : -1)
                .sum();
    }
}
