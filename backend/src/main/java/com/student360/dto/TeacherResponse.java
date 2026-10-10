package com.student360.dto;

public record TeacherResponse(
    Long id,
    String teacherCode,
    String firstName,
    String lastName,
    String email,
    String phone,
    String designation,
    String qualification,
    Long departmentId,
    String departmentName,
    Long userId
) {}
