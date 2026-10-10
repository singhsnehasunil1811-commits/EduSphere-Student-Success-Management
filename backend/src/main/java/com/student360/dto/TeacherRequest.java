package com.student360.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record TeacherRequest(
    @NotBlank(message = "Teacher code is required") String teacherCode,
    @NotBlank(message = "First name is required") String firstName,
    @NotBlank(message = "Last name is required") String lastName,
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @Pattern(regexp = "^$|\\d{10}", message = "Phone must be 10 digits") String phone,
    String designation,
    String qualification,
    @NotNull(message = "Department ID is required") Long departmentId,
    Long userId
) {}
