package com.madan.phantomduplicate.entity.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionResponseDto {
    private Long dbId;
    private String submissionId;
    private String payload;
    private String requestId;
    private String status;
}
