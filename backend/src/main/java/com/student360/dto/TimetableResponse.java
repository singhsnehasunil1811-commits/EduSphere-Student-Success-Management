package com.student360.dto;

import java.time.LocalTime;

public record TimetableResponse(
    Long id,
    String day,
    LocalTime startTime,
    LocalTime endTime,
    Long subjectId,
    String subjectName,
    String subjectCode,
    Long teacherId,
    String teacherName,
    String room
) {}
