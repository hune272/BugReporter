package com.bug_reporter.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bug_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BugTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}