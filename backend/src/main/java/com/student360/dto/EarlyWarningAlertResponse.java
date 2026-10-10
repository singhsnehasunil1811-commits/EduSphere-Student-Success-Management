package com.student360.dto;

import java.util.List;

public record EarlyWarningAlertResponse(
    String riskLevel,
    String primaryReason,
    List<String> warnings,
    String recommendedIntervention,
    int recoveryClassesNeeded,
    String recoveryMessage
) {}
