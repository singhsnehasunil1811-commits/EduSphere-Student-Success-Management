package com.student360.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseRequest(
    @NotBlank(message = "Course code is required") String courseCode,
    @NotBlank(message = "Course name is required") String name,
    @Min(value = 1, message = "Duration must be at least 1 year")
    @Max(value = 6, message = "Duration must not exceed 6 years")
    int durationYears,
    @NotNull(message = "Department ID is required") Long departmentId
) {}
