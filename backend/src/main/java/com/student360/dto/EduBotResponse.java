package com.student360.dto;

import java.util.List;
import java.util.Map;

public record EduBotResponse(
    String query,
    String response,
    String category,
    Map<String, Object> contextualData,
    List<String> suggestedFollowups
) {}
