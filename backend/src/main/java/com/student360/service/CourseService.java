package com.student360.service;

import com.student360.dto.CourseRequest;
import com.student360.dto.CourseResponse;
import com.student360.entity.Course;
import java.util.List;

public interface CourseService {
    List<CourseResponse> getAll();
    List<CourseResponse> getByDepartment(Long departmentId);
    CourseResponse getById(Long id);
    Course getEntityById(Long id);
    CourseResponse create(CourseRequest request);
    CourseResponse update(Long id, CourseRequest request);
    void delete(Long id);
}
