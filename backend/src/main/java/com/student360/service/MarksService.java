package com.student360.service;

import com.student360.dto.MarksRequest;
import com.student360.dto.MarksResponse;
import com.student360.dto.StudentGpaResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface MarksService {
    MarksResponse createOrUpdate(MarksRequest request);
    MarksResponse getByStudentAndSubject(Long studentId, Long subjectId, Authentication auth);
    List<MarksResponse> getByStudent(Long studentId, Authentication auth);
    List<MarksResponse> getByStudent(Long studentId, String currentUsername);
    List<MarksResponse> getBySubject(Long subjectId);
    StudentGpaResponse getStudentGpa(Long studentId, Authentication auth);
    StudentGpaResponse getStudentGpa(Long studentId, String currentUsername);
    List<MarksResponse> getMyMarks(Authentication auth);
    StudentGpaResponse getMyGpa(Authentication auth);
}
