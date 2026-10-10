package com.student360.service;

import com.student360.dto.TimetableRequest;
import com.student360.dto.TimetableResponse;
import java.util.List;

public interface TimetableService {
    TimetableResponse create(TimetableRequest request);
    TimetableResponse update(Long id, TimetableRequest request);
    void delete(Long id);
    List<TimetableResponse> getAll();
    List<TimetableResponse> getByDay(String day);
    List<TimetableResponse> getByCourse(Long courseId);
    List<TimetableResponse> getByTeacher(Long teacherId);
}
