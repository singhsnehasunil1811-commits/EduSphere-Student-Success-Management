package com.student360.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record StudentProfileResponse(
    Long id,
    String studentCode,
    String rollNumber,
    String firstName,
    String lastName,
    String email,
    String phone,
    String status,
    LocalDate admissionDate,
    int year,
    int semester,
    Long departmentId,
    String departmentName,
    Long courseId,
    String courseName,
    double attendancePercentage,
    boolean belowAttendanceThreshold,
    double averageMarks,
    double cgpa,
    String feeStatus,
    double totalFees,
    double paidFees,
    double remainingFees,
    List<MarksResponse> recentMarks,
    List<Map<String, Object>> recentActivity
) {}
