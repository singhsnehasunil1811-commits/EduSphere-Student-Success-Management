package com.student360.dto;

import com.student360.entity.AttendanceStatus;
import java.time.LocalDate;

public record AttendanceRecordResponse(
    Long id,
    Long studentId,
    String studentRoll,
    String studentName,
    Long subjectId,
    String subjectCode,
    String subjectName,
    LocalDate date,
    AttendanceStatus status,
    String remarks
) {}
