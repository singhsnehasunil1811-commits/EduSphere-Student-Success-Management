package com.student360.dto;

public record SubjectResponse(
    Long id,
    String code,
    String name,
    int credits,
    int semester,
    Long courseId,
    String courseName,
    Long teacherId,
    String teacherName
) {}
