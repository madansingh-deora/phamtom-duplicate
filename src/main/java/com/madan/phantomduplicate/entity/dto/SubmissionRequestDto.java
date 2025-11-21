package com.madan.phantomduplicate.entity.dto;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionRequestDto {
    private String submissionId;
    private Map<String, Object> payload;
}
