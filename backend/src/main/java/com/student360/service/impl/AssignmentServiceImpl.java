package com.student360.service.impl;

import com.student360.dto.AssignmentRequest;
import com.student360.dto.AssignmentResponse;
import com.student360.dto.SubmissionRequest;
import com.student360.dto.SubmissionResponse;
import com.student360.entity.Assignment;
import com.student360.entity.AssignmentSubmission;
import com.student360.entity.Student;
import com.student360.entity.Subject;
import com.student360.entity.Teacher;
import com.student360.exception.ResourceNotFoundException;
import com.student360.exception.DuplicateResourceException;
import com.student360.repository.AssignmentRepository;
import com.student360.repository.AssignmentSubmissionRepository;
import com.student360.repository.StudentRepository;
import com.student360.repository.SubjectRepository;
import com.student360.repository.TeacherRepository;
import com.student360.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    public AssignmentResponse create(AssignmentRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Teacher teacher = teacherRepository.findById(request.teacherId())
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        Assignment assignment = new Assignment();
        assignment.setTitle(request.title());
        assignment.setDescription(request.description());
        assignment.setSubject(subject);
        assignment.setDeadline(request.deadline());
        assignment.setMaxMarks(request.maxMarks());
        assignment.setCreatedBy(teacher);
        
        Assignment saved = assignmentRepository.save(assignment);
        return mapToResponse(saved);
    }

    @Override
    public AssignmentResponse update(Long id, AssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
            
        Subject subject = subjectRepository.findById(request.subjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Teacher teacher = teacherRepository.findById(request.teacherId())
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        assignment.setTitle(request.title());
        assignment.setDescription(request.description());
        assignment.setSubject(subject);
        assignment.setDeadline(request.deadline());
        assignment.setMaxMarks(request.maxMarks());
        assignment.setCreatedBy(teacher);
        
        Assignment saved = assignmentRepository.save(assignment);
        return mapToResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Assignment not found");
        }
        assignmentRepository.deleteById(id);
    }

    @Override
    public List<AssignmentResponse> getAll() {
        return assignmentRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public AssignmentResponse getById(Long id) {
        return assignmentRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
    }

    @Override
    public List<AssignmentResponse> getBySubject(Long subjectId) {
        return assignmentRepository.findBySubjectId(subjectId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public SubmissionResponse submit(SubmissionRequest request) {
        Assignment assignment = assignmentRepository.findById(request.assignmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        Student student = studentRepository.findById(request.studentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            
        if (submissionRepository.findByStudentIdAndAssignmentId(student.getId(), assignment.getId()).isPresent()) {
            throw new DuplicateResourceException("Submission already exists for this assignment");
        }
        
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setStudent(student);
        submission.setAssignment(assignment);
        submission.setSubmittedAt(LocalDateTime.now());
        
        if (submission.getSubmittedAt().toLocalDate().isAfter(assignment.getDeadline())) {
            submission.setStatus("LATE");
        } else {
            submission.setStatus("SUBMITTED");
        }
        
        AssignmentSubmission saved = submissionRepository.save(submission);
        return mapToSubmissionResponse(saved);
    }

    @Override
    public List<SubmissionResponse> getSubmissions(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId).stream()
            .map(this::mapToSubmissionResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<SubmissionResponse> getStudentSubmissions(Long studentId) {
        return submissionRepository.findByStudentId(studentId).stream()
            .map(this::mapToSubmissionResponse)
            .collect(Collectors.toList());
    }

    private AssignmentResponse mapToResponse(Assignment assignment) {
        return new AssignmentResponse(
            assignment.getId(),
            assignment.getTitle(),
            assignment.getDescription(),
            assignment.getSubject().getId(),
            assignment.getSubject().getName(),
            assignment.getDeadline(),
            assignment.getMaxMarks(),
            assignment.getCreatedBy().getId(),
            assignment.getCreatedBy().getUser().getFirstName() + " " + assignment.getCreatedBy().getUser().getLastName(),
            assignment.getCreatedAt()
        );
    }
    
    private SubmissionResponse mapToSubmissionResponse(AssignmentSubmission submission) {
        return new SubmissionResponse(
            submission.getId(),
            submission.getStudent().getId(),
            submission.getStudent().getUser().getFirstName() + " " + submission.getStudent().getUser().getLastName(),
            submission.getAssignment().getId(),
            submission.getAssignment().getTitle(),
            submission.getSubmittedAt(),
            submission.getStatus()
        );
    }
}
