package com.student360.dto;

public record SubjectAttendanceSummary(
    Long subjectId,
    String subjectCode,
    String subjectName,
    long total,
    long attended,
    double percentage,
    boolean belowThreshold
) {}
