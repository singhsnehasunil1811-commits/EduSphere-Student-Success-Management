package com.student360.dto;

public record MonthlyAttendanceSummary(
    String month,
    long total,
    long attended,
    double percentage
) {}
