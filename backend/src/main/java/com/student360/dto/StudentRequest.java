package com.student360.dto;

import jakarta.validation.constraints.*;

public record StudentRequest(
    @NotBlank(message = "Student ID is required") String studentCode,
    @NotBlank(message = "Roll number is required") String rollNumber,
    @NotBlank(message = "First name is required") String firstName,
    @NotBlank(message = "Last name is required") String lastName,
    @NotBlank(message = "Email is required") @Email(message = "Email is not valid") String email,
    @Pattern(regexp = "^$|\\d{10}", message = "Phone must be 10 digits") String phone,
    @NotNull(message = "Department is required") Long departmentId,
    Long courseId,
    @Min(value = 1, message = "Year must be 1-4") @Max(value = 4, message = "Year must be 1-4") int year,
    @Min(value = 1, message = "Semester must be 1-8") @Max(value = 8, message = "Semester must be 1-8") int semester,
    String status,
    Long userId
) {}
