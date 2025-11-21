package com.madan.phantomduplicate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name="submissions" ,uniqueConstraints = {
        @UniqueConstraint(name = "uk_submission_id", columnNames = {"submission_id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="submission_id",nullable = false,length = 255)
    private String submissionId;

    @Lob
    @Column(name="payload",columnDefinition = "text")
    private String payload;

    @Column(name="created_at",nullable = false)
    private OffsetDateTime createdAt;
}
