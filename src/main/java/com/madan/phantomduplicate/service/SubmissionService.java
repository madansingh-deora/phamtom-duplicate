package com.madan.phantomduplicate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madan.phantomduplicate.entity.Submission;
import com.madan.phantomduplicate.repository.SubmissionRepository;
import com.madan.phantomduplicate.util.PgAdvisoryLock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;


@Service
public class SubmissionService {
    private final SubmissionRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;


    private final Logger log = LoggerFactory.getLogger(SubmissionService.class);


    public SubmissionService(SubmissionRepository repository, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public SubmissionResponse handleSubmission(String submissionId, Map<String, Object> payload, String requestId) {
        String normalizedId = normalizeSubmissionId(submissionId);
        long lockKey = computeLockKey(normalizedId);
        PgAdvisoryLock lock = new PgAdvisoryLock(jdbcTemplate, lockKey);


        log.info("[request_id={}] attempting to acquire advisory lock for submissionId={} (key={})", requestId, normalizedId, lockKey);
        lock.lock();
        try {
            Optional<Submission> existing = repository.findBySubmissionId(normalizedId);
            if (existing.isPresent()) {
                log.info("[request_id={}] found existing submission id={} db_id={}", requestId, normalizedId, existing.get().getId());
                return new SubmissionResponse(existing.get(), false, requestId);
            }



            Submission s = new Submission();
            s.setSubmissionId(normalizedId);
            try {
                s.setPayload(objectMapper.writeValueAsString(payload));
            } catch (Exception e) {
                s.setPayload("{}");
            }
            s.setCreatedAt(OffsetDateTime.now());
            try {
                Submission saved = repository.save(s);
                log.info("[request_id={}] inserted submission id={} db_id={}", requestId, normalizedId, saved.getId());
                return new SubmissionResponse(saved, true, requestId);
            } catch (DataIntegrityViolationException dive) {
                log.warn("[request_id={}] DataIntegrityViolation while saving submissionId={}, falling back to read", requestId, normalizedId);
                Optional<Submission> readBack = repository.findBySubmissionId(normalizedId);
                if (readBack.isPresent()) {
                    return new SubmissionResponse(readBack.get(), false, requestId);
                } else {
                    throw dive;
                }
            }
        } finally {
            lock.unlock();
            log.info("[request_id={}] released advisory lock for submissionId={}", requestId, normalizedId);
        }
    }


    private String normalizeSubmissionId(String id) {
        if (id == null) return "";
        return id.trim();
    }


    private long computeLockKey(String id) {
        long h = 1125899906842597L;
        for (int i = 0; i < id.length(); i++) {
            h = 31*h + id.charAt(i);
        }
        return h;
    }


    public static class SubmissionResponse {
        public final Submission submission;
        public final boolean created;
        public final String requestId;


        public SubmissionResponse(Submission submission, boolean created, String requestId) {
            this.submission = submission;
            this.created = created;
            this.requestId = requestId;
        }
    }
}
