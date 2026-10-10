package com.student360.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record AssignmentRequest(
    @NotBlank(message = "Title is required")
    String title,
    
    @NotBlank(message = "Description is required")
    String description,
    
    @NotNull(message = "Subject ID is required")
    Long subjectId,
    
    @NotNull(message = "Deadline is required")
    LocalDate deadline,
    
    @Positive(message = "Max marks must be positive")
    int maxMarks,
    
    @NotNull(message = "Teacher ID is required")
    Long teacherId
) {}
