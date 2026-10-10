package com.student360.dto;

public record DepartmentResponse(
    Long id,
    String name,
    String code,
    String hod,
    String description
) {}
