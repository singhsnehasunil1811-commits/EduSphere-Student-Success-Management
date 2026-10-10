package com.student360.dto;

public record SuccessScoreResponse(
    double overallScore,
    double attendanceScore,
    double academicScore,
    double assignmentScore,
    double trendScore,
    String scoreTrend,
    String status,
    String statusMessage
) {}
