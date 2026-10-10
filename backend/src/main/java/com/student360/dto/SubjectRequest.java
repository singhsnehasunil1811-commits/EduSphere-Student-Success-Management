package com.student360.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubjectRequest(
    @NotBlank(message = "Subject code is required") String code,
    @NotBlank(message = "Subject name is required") String name,
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits must not exceed 10")
    int credits,
    @Min(value = 1, message = "Semester must be between 1 and 8")
    @Max(value = 8, message = "Semester must be between 1 and 8")
    int semester,
    @NotNull(message = "Course ID is required") Long courseId,
    Long teacherId
) {}
