package com.bug_reporter.backend.model;

import com.bug_reporter.backend.model.enums.BugStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bugs")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column(columnDefinition = "TEXT")
    private String text;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column
    private String picture;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    @OneToMany(mappedBy = "bug", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    @OneToMany(mappedBy = "bug", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes;
    @OneToMany(mappedBy = "bug", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BugTag> bugTags;
}