package com.student360.service.impl;

import com.student360.dto.MarksRequest;
import com.student360.dto.MarksResponse;
import com.student360.dto.SemesterGpa;
import com.student360.dto.StudentGpaResponse;
import com.student360.entity.Marks;
import com.student360.entity.Student;
import com.student360.entity.Subject;
import com.student360.entity.User;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.MarksRepository;
import com.student360.repository.StudentRepository;
import com.student360.repository.SubjectRepository;
import com.student360.repository.UserRepository;
import com.student360.service.GradingUtil;
import com.student360.service.MarksService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MarksServiceImpl implements MarksService {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public MarksServiceImpl(MarksRepository marksRepository,
                            StudentRepository studentRepository,
                            SubjectRepository subjectRepository,
                            UserRepository userRepository) {
        this.marksRepository = marksRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public MarksResponse createOrUpdate(MarksRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        Marks marks = marksRepository.findByStudentIdAndSubjectId(request.studentId(), request.subjectId())
                .orElse(new Marks());

        marks.setStudent(student);
        marks.setSubject(subject);
        marks.setInternal(request.internal());
        marks.setAssignment(request.assignment());
        marks.setMidterm(request.midterm());
        marks.setEndSem(request.endSem());

        Marks saved = marksRepository.save(marks);
        return mapToResponse(saved);
    }

    @Override
    public MarksResponse getByStudentAndSubject(Long studentId, Long subjectId, Authentication auth) {
        checkOwnership(studentId, auth != null ? auth.getName() : null);
        Marks marks = marksRepository.findByStudentIdAndSubjectId(studentId, subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Marks not found"));
        return mapToResponse(marks);
    }

    @Override
    public List<MarksResponse> getByStudent(Long studentId, Authentication auth) {
        return getByStudent(studentId, auth != null ? auth.getName() : null);
    }

    @Override
    public List<MarksResponse> getByStudent(Long studentId, String currentUsername) {
        checkOwnership(studentId, currentUsername);
        return marksRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarksResponse> getBySubject(Long subjectId) {
        return marksRepository.findBySubjectId(subjectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StudentGpaResponse getStudentGpa(Long studentId, Authentication auth) {
        return getStudentGpa(studentId, auth != null ? auth.getName() : null);
    }

    @Override
    public StudentGpaResponse getStudentGpa(Long studentId, String currentUsername) {
        checkOwnership(studentId, currentUsername);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<Marks> allMarks = marksRepository.findByStudentIdOrderBySubjectSemesterAsc(studentId);
        List<MarksResponse> marksResponses = allMarks.stream().map(this::mapToResponse).toList();

        Map<Integer, List<MarksResponse>> marksBySem = marksResponses.stream()
                .collect(Collectors.groupingBy(m -> getSemesterBySubjectId(m.subjectId(), allMarks)));

        List<SemesterGpa> semesters = new ArrayList<>();
        for (Map.Entry<Integer, List<MarksResponse>> entry : marksBySem.entrySet()) {
            int sem = entry.getKey();
            List<MarksResponse> semMarks = entry.getValue();
            double gpa = GradingUtil.computeGpa(semMarks);
            int credits = semMarks.stream().mapToInt(MarksResponse::credits).sum();
            semesters.add(new SemesterGpa(sem, gpa, credits, semMarks));
        }

        semesters.sort(Comparator.comparingInt(SemesterGpa::semester));

        double cgpa = GradingUtil.computeCgpa(semesters);
        double overallGpa = semesters.isEmpty() ? 0.0 : GradingUtil.computeGpa(marksResponses);

        return new StudentGpaResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                student.getRollNumber(),
                overallGpa,
                cgpa,
                semesters
        );
    }

    private int getSemesterBySubjectId(Long subjectId, List<Marks> allMarks) {
        return allMarks.stream()
                .filter(m -> m.getSubject().getId().equals(subjectId))
                .findFirst()
                .map(m -> m.getSubject().getSemester())
                .orElse(0);
    }

    @Override
    public List<MarksResponse> getMyMarks(Authentication auth) {
        User user = getUser(auth);
        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return getByStudent(student.getId(), auth);
    }

    @Override
    public StudentGpaResponse getMyGpa(Authentication auth) {
        User user = getUser(auth);
        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return getStudentGpa(student.getId(), auth);
    }

    private MarksResponse mapToResponse(Marks marks) {
        int total = GradingUtil.computeTotal(marks.getInternal(), marks.getAssignment(), marks.getMidterm(), marks.getEndSem());
        double percentage = GradingUtil.computePercentage(total);
        String grade = GradingUtil.computeGrade(percentage);
        double gradePoints = GradingUtil.computeGradePoints(grade);

        Student student = marks.getStudent();
        String studentName = student.getFirstName() + " " + student.getLastName();

        return new MarksResponse(
                marks.getId(),
                student.getId(),
                studentName,
                student.getRollNumber(),
                marks.getSubject().getId(),
                marks.getSubject().getCode(),
                marks.getSubject().getName(),
                marks.getSubject().getCredits(),
                marks.getInternal(),
                marks.getAssignment(),
                marks.getMidterm(),
                marks.getEndSem(),
                total,
                percentage,
                grade,
                gradePoints
        );
    }

    private void checkOwnership(Long studentId, String currentUsername) {
        if (currentUsername == null) return;
        User user = userRepository.findByEmail(currentUsername).orElse(null);
        if (user != null && "STUDENT".equalsIgnoreCase(user.getRole())) {
            Student student = studentRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
            if (!student.getId().equals(studentId)) {
                throw new AccessDeniedException("You can only view your own marks");
            }
        }
    }

    private User getUser(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
