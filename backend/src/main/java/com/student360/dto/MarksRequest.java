package com.student360.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MarksRequest(
    @NotNull(message = "Student ID is required")
    Long studentId,

    @NotNull(message = "Subject ID is required")
    Long subjectId,

    @Min(value = 0, message = "Internal marks cannot be less than 0")
    @Max(value = 20, message = "Internal marks cannot exceed 20")
    int internal,

    @Min(value = 0, message = "Assignment marks cannot be less than 0")
    @Max(value = 20, message = "Assignment marks cannot exceed 20")
    int assignment,

    @Min(value = 0, message = "Midterm marks cannot be less than 0")
    @Max(value = 30, message = "Midterm marks cannot exceed 30")
    int midterm,

    @Min(value = 0, message = "End semester marks cannot be less than 0")
    @Max(value = 30, message = "End semester marks cannot exceed 30")
    int endSem
) {}
