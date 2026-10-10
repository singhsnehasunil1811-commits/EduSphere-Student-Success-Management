package com.student360.dto;

public record CourseResponse(
    Long id,
    String courseCode,
    String name,
    int durationYears,
    Long departmentId,
    String departmentName
) {}
