package com.student360.service;

import com.student360.dto.TeacherRequest;
import com.student360.dto.TeacherResponse;
import com.student360.entity.Teacher;
import java.util.List;

public interface TeacherService {
    List<TeacherResponse> getAll();
    List<TeacherResponse> getByDepartment(Long departmentId);
    TeacherResponse getById(Long id);
    Teacher getEntityById(Long id);
    Teacher getEntityByUserId(Long userId);
    Teacher getEntityByUserEmail(String email);
    TeacherResponse create(TeacherRequest request);
    TeacherResponse update(Long id, TeacherRequest request);
    void delete(Long id);
}
