package com.student360.dto;

import com.student360.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public record StudentAttendanceEntry(
    @NotNull(message = "Student ID is required") Long studentId,
    @NotNull(message = "Attendance status is required") AttendanceStatus status,
    String remarks
) {}
