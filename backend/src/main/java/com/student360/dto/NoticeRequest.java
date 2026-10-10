package com.student360.dto;

import com.student360.entity.NoticePriority;
import com.student360.entity.TargetAudience;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NoticeRequest(
    @NotBlank(message = "Title is required")
    String title,
    
    @NotBlank(message = "Content is required")
    String content,
    
    @NotNull(message = "Priority is required")
    NoticePriority priority,
    
    @NotNull(message = "Target audience is required")
    TargetAudience targetAudience
) {}
