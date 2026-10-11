package com.student360.dto;

import java.util.List;

public record GradeSimulationResponse(
        Long studentId,
        String studentName,
        double currentCgpa,
        double targetCgpa,
        double requiredSemesterGpa,
        boolean isAchievable,
        String feasibilityStatus, // REALISTIC, CHALLENGING, MATHEMATICALLY_UNREACHABLE
        String advice,
        List<SubjectTarget> subjectBreakdown
) {
    public record SubjectTarget(
            Long subjectId,
            String subjectCode,
            String subjectName,
            int credits,
            int currentInternalsTotal, // internal + assignment + midterm (out of 70)
            int minEndSemMarksNeeded,  // out of 30
            String recommendedGrade
    ) {}
}
