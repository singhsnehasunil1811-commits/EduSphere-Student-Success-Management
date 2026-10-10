package com.student360.dto;

import java.time.LocalDate;

public record StudentResponse(
    Long id,
    String studentCode,
    String rollNumber,
    String firstName,
    String lastName,
    String email,
    String phone,
    Long departmentId,
    String departmentName,
    Long courseId,
    String courseName,
    int year,
    int semester,
    String status,
    LocalDate admissionDate,
    Long userId
) {}
