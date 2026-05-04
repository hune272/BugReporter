package com.bug_reporter.backend.dto.response;

import com.bug_reporter.backend.model.enums.BugStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BugResponse(
        Long id,
        String title,
        String text,
        String picture,
        BugStatus status,
        LocalDateTime createdAt,
        UserSummary author,
        List<TagSummary> tags,
        int voteCount,
        int commentCount
) {}
