package com.student360.dto;

import java.util.List;
import java.util.Map;

public record StudentSuccessSummaryResponse(
    Long studentId,
    String studentName,
    String studentRoll,
    SuccessScoreResponse successScore,
    EarlyWarningAlertResponse warningAlert,
    List<String> strengths,
    List<String> weaknesses,
    List<String> recommendations,
    Map<String, Object> workloadSummary
) {}
