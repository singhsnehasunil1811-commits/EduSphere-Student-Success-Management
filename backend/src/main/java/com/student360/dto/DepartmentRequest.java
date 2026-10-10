package com.student360.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
    @NotBlank(message = "Department name is required") String name,
    String code,
    String hod,
    String description
) {}
