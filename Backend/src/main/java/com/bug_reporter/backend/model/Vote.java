package com.bug_reporter.backend.model;

import com.bug_reporter.backend.model.enums.VoteType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "votes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false)
    private VoteType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id")
    private Bug bug;
}