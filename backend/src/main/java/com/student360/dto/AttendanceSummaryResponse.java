package com.student360.dto;

import java.util.List;

public record AttendanceSummaryResponse(
    Long studentId,
    String studentName,
    String studentRoll,
    long totalClasses,
    long attendedClasses,
    double overallPercentage,
    boolean belowThreshold,
    double threshold,
    List<SubjectAttendanceSummary> subjectSummaries,
    List<MonthlyAttendanceSummary> monthlySummaries
) {}
