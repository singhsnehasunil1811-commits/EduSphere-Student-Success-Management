package com.student360.service;

import com.student360.dto.SubjectRequest;
import com.student360.dto.SubjectResponse;
import com.student360.entity.Subject;
import java.util.List;

public interface SubjectService {
    List<SubjectResponse> getAll();
    List<SubjectResponse> getByCourse(Long courseId);
    List<SubjectResponse> getByTeacher(Long teacherId);
    List<SubjectResponse> getByCourseAndSemester(Long courseId, int semester);
    SubjectResponse getById(Long id);
    Subject getEntityById(Long id);
    SubjectResponse create(SubjectRequest request);
    SubjectResponse update(Long id, SubjectRequest request);
    void delete(Long id);
}
