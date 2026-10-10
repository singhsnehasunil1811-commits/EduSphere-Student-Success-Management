package com.student360.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @NotBlank(message = "Full name is required") String fullName,
    @NotBlank(message = "Password is required") String password,
    @NotBlank(message = "Role is required") String role
) {}
