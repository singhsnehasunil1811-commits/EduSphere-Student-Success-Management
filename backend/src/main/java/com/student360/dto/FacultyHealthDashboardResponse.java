package com.student360.dto;

import java.util.List;

public record FacultyHealthDashboardResponse(
    long totalStudents,
    long onTrackCount,
    long needsAttentionCount,
    long criticalCount,
    double averageAttendance,
    double averageAcademicPerformance,
    List<StudentSuccessSummaryResponse> interventionStudents
) {}
