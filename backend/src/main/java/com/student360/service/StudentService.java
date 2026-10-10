package com.student360.service;

import com.student360.dto.PageResponse;
import com.student360.dto.StudentProfileResponse;
import com.student360.dto.StudentRequest;
import com.student360.dto.StudentResponse;
import com.student360.entity.Student;
import java.util.List;

public interface StudentService {
    List<StudentResponse> search(String q);
    List<StudentResponse> getAll();
    List<StudentResponse> getByDepartment(Long departmentId);
    List<StudentResponse> getByCourse(Long courseId);
    List<StudentResponse> getByCourseAndSemester(Long courseId, int semester);
    PageResponse<StudentResponse> getFilteredStudents(
        String q,
        Long departmentId,
        Long courseId,
        Integer semester,
        Integer year,
        String status,
        int page,
        int size,
        String sortBy,
        String sortDir
    );
    StudentResponse get(Long id);
    Student getEntity(Long id);
    Student getEntityByUserId(Long userId);
    Student getEntityByUserEmail(String email);
    StudentProfileResponse getStudentProfile(Long id, String currentUsername);
    StudentProfileResponse getMyProfile(String currentUsername);
    StudentResponse create(StudentRequest request);
    StudentResponse update(Long id, StudentRequest request);
    void delete(Long id);
    StudentResponse toResponse(Student student);
}
