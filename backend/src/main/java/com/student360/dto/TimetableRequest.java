package com.student360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record TimetableRequest(
    @NotBlank(message = "Day is required")
    String day,
    
    @NotNull(message = "Start time is required")
    LocalTime startTime,
    
    @NotNull(message = "End time is required")
    LocalTime endTime,
    
    @NotNull(message = "Subject ID is required")
    Long subjectId,
    
    @NotNull(message = "Teacher ID is required")
    Long teacherId,
    
    @NotBlank(message = "Room is required")
    String room
) {}
