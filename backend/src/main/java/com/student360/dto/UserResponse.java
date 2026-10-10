package com.student360.dto;

public record UserResponse(
    Long id,
    String email,
    String fullName,
    String role,
    boolean enabled
) {}
