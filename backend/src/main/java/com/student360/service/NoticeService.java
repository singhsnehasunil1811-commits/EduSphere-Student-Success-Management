package com.student360.service;

import com.student360.dto.NoticeRequest;
import com.student360.dto.NoticeResponse;
import com.student360.entity.TargetAudience;
import java.util.List;

public interface NoticeService {
    NoticeResponse create(NoticeRequest request, String username);
    NoticeResponse update(Long id, NoticeRequest request);
    void delete(Long id);
    List<NoticeResponse> getAll();
    NoticeResponse getById(Long id);
    List<NoticeResponse> getRecent();
    List<NoticeResponse> getForAudience(TargetAudience audience);
}
