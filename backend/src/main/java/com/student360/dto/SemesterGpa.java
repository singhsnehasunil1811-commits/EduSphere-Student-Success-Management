package com.student360.dto;

import java.util.List;

public record SemesterGpa(
    int semester,
    double gpa,
    int totalCredits,
    List<MarksResponse> subjects
) {}
