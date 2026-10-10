package com.student360.dto;

import java.util.List;

public record StudentGpaResponse(
    Long studentId,
    String studentName,
    String studentRoll,
    double gpa,
    double cgpa,
    List<SemesterGpa> semesters
) {}
