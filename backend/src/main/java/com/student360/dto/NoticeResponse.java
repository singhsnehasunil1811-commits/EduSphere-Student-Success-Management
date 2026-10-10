package com.student360.dto;

import com.student360.entity.NoticePriority;
import com.student360.entity.TargetAudience;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record NoticeResponse(
    Long id,
    String title,
    String content,
    NoticePriority priority,
    LocalDate date,
    TargetAudience targetAudience,
    String createdByName,
    LocalDateTime createdAt
) {}
