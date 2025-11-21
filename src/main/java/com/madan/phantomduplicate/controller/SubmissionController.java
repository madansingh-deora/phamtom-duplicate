package com.madan.phantomduplicate.controller;

import com.madan.phantomduplicate.entity.Submission;
import com.madan.phantomduplicate.entity.dto.SubmissionRequestDto;
import com.madan.phantomduplicate.entity.dto.SubmissionResponseDto;
import com.madan.phantomduplicate.service.SubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/submit")
public class SubmissionController {

    private final SubmissionService service;
    private final Logger log = LoggerFactory.getLogger(SubmissionController.class);

    public SubmissionController(SubmissionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> submit (@RequestBody SubmissionRequestDto submissionRequest){

        String requestId = UUID.randomUUID().toString();
        log.info("[request_id={}] incoming submit: submission_id{}",
                requestId,submissionRequest.getSubmissionId());

        var resp = service.handleSubmission(
                submissionRequest.getSubmissionId(),
                submissionRequest.getPayload(),
                requestId);

        SubmissionResponseDto responseDto = new SubmissionResponseDto(
                resp.submission.getId(),
                resp.submission.getSubmissionId(),
                resp.submission.getPayload(),
                resp.requestId,
                resp.created?"created":"already_exists"
        );

        return resp.created
                ? ResponseEntity.status(HttpStatus.CREATED).body(responseDto)
                : ResponseEntity.ok(responseDto);

    }
}
