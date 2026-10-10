package com.student360.dto;

import jakarta.validation.constraints.NotNull;

public record SubmissionRequest(
    @NotNull(message = "Student ID is required")
    Long studentId,
    
    @NotNull(message = "Assignment ID is required")
    Long assignmentId
) {}
