package com.bug_reporter.backend.dto.mapper;

import com.bug_reporter.backend.dto.request.BugCreateRequest;
import com.bug_reporter.backend.dto.response.BugResponse;
import com.bug_reporter.backend.dto.response.TagSummary;
import com.bug_reporter.backend.model.Bug;
import com.bug_reporter.backend.model.BugTag;
import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.model.enums.VoteType;

import java.util.List;

public final class BugMapper {

    private BugMapper() {
    }

    public static BugResponse toResponse(Bug bug) {
        return toResponse(
                bug,
                computeVoteCount(bug.getVotes()),
                bug.getComments() == null ? 0 : bug.getComments().size()
        );
    }

    public static BugResponse toResponse(Bug bug, int voteCount, int commentCount) {
        List<TagSummary> tags = bug.getBugTags() == null
                ? List.of()
                : bug.getBugTags().stream()
                .map(BugTag::getTag)
                .map(TagMapper::toSummary)
                .toList();

        return toResponse(bug, tags, voteCount, commentCount);
    }

    public static BugResponse toResponse(Bug bug, List<TagSummary> tags, int voteCount, int commentCount) {
        return toResponse(bug, tags, voteCount, commentCount, 0.0);
    }

    public static BugResponse toResponse(Bug bug, List<TagSummary> tags, int voteCount, int commentCount, double authorScore) {

        return new BugResponse(
                bug.getId(),
                bug.getTitle(),
                bug.getText(),
                bug.getPicture(),
                bug.getStatus(),
                bug.getCreatedAt(),
                UserMapper.toSummary(bug.getAuthor(), authorScore),
                tags == null ? List.of() : tags,
                voteCount,
                commentCount
        );
    }

    public static Bug toEntity(BugCreateRequest request, User author) {
        Bug bug = new Bug();
        bug.setTitle(request.title());
        bug.setText(request.text());
        bug.setPicture(request.picture());
        bug.setAuthor(author);
        return bug;
    }

    private static int computeVoteCount(List<Vote> votes) {
        if (votes == null) return 0;
        int up = (int) votes.stream().filter(vote -> vote.getType() == VoteType.UPVOTE).count();
        int down = (int) votes.stream().filter(vote -> vote.getType() == VoteType.DOWNVOTE).count();
        return up - down;
    }
}
