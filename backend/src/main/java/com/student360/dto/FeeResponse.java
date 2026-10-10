package com.student360.dto;

import java.time.LocalDate;

public record FeeResponse(
    Long id,
    Long studentId,
    String studentName,
    String studentRoll,
    String academicYear,
    double totalFees,
    double paidAmount,
    double remaining,
    LocalDate dueDate,
    String remarks,
    String status
) {}
