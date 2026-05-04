package com.bug_reporter.backend.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String comment,
        String imageUrl,
        LocalDateTime createdAt,
        UserSummary author,
        Long bugId,
        int voteCount
) {}
