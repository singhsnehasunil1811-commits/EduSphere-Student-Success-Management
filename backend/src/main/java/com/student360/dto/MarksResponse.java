package com.student360.dto;

public record MarksResponse(
    Long id,
    Long studentId,
    String studentName,
    String studentRoll,
    Long subjectId,
    String subjectCode,
    String subjectName,
    int credits,
    int internal,
    int assignment,
    int midterm,
    int endSem,
    int total,
    double percentage,
    String grade,
    double gradePoints
) {}
