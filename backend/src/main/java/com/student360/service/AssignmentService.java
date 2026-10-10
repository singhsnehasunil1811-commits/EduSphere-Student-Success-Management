package com.student360.service;

import com.student360.dto.AssignmentRequest;
import com.student360.dto.AssignmentResponse;
import com.student360.dto.SubmissionRequest;
import com.student360.dto.SubmissionResponse;
import java.util.List;

public interface AssignmentService {
    AssignmentResponse create(AssignmentRequest request);
    AssignmentResponse update(Long id, AssignmentRequest request);
    void delete(Long id);
    List<AssignmentResponse> getAll();
    AssignmentResponse getById(Long id);
    List<AssignmentResponse> getBySubject(Long subjectId);
    
    SubmissionResponse submit(SubmissionRequest request);
    List<SubmissionResponse> getSubmissions(Long assignmentId);
    List<SubmissionResponse> getStudentSubmissions(Long studentId);
}
