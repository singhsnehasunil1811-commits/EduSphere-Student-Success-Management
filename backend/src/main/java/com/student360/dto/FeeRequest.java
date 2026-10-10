package com.student360.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record FeeRequest(
    @NotNull(message = "Student ID is required")
    Long studentId,
    
    @NotBlank(message = "Academic year is required")
    String academicYear,
    
    @Positive(message = "Total fees must be positive")
    double totalFees,
    
    @NotNull(message = "Due date is required")
    LocalDate dueDate,
    
    String remarks
) {}
