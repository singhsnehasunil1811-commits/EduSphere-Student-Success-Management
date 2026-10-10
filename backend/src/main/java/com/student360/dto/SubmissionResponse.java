package com.student360.dto;

import java.time.LocalDateTime;

public record SubmissionResponse(
    Long id,
    Long studentId,
    String studentName,
    Long assignmentId,
    String assignmentTitle,
    LocalDateTime submittedAt,
    String status
) {}
