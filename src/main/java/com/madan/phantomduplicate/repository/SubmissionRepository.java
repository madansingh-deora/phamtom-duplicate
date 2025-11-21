package com.madan.phantomduplicate.repository;

import com.madan.phantomduplicate.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission,Long> {
    Optional<Submission> findBySubmissionId(String submissionId);
}
