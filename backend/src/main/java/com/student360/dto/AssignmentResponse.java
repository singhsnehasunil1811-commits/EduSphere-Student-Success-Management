package com.student360.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssignmentResponse(
    Long id,
    String title,
    String description,
    Long subjectId,
    String subjectName,
    LocalDate deadline,
    int maxMarks,
    Long teacherId,
    String teacherName,
    LocalDateTime createdAt
) {}
