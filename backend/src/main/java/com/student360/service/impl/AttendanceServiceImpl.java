package com.student360.service.impl;

import com.student360.dto.*;
import com.student360.entity.*;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.AttendanceRepository;
import com.student360.repository.StudentRepository;
import com.student360.repository.SubjectRepository;
import com.student360.repository.UserRepository;
import com.student360.service.AttendanceService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepo;
    private final StudentRepository studentRepo;
    private final SubjectRepository subjectRepo;
    private final UserRepository userRepo;

    @Value("${student360.attendance.threshold:75.0}")
    private double threshold;

    public AttendanceServiceImpl(
        AttendanceRepository attendanceRepo,
        StudentRepository studentRepo,
        SubjectRepository subjectRepo,
        UserRepository userRepo
    ) {
        this.attendanceRepo = attendanceRepo;
        this.studentRepo = studentRepo;
        this.subjectRepo = subjectRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public List<AttendanceRecordResponse> bulkMark(BulkAttendanceRequest request) {
        Subject subject = subjectRepo.findById(request.subjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id " + request.subjectId()));

        List<AttendanceRecordResponse> responses = new ArrayList<>();

        for (StudentAttendanceEntry entry : request.entries()) {
            Student student = studentRepo.findById(entry.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + entry.studentId()));

            Attendance attendance = attendanceRepo
                .findByStudentIdAndSubjectIdAndDate(student.getId(), subject.getId(), request.date())
                .orElseGet(() -> {
                    Attendance a = new Attendance();
                    a.setStudent(student);
                    a.setSubject(subject);
                    a.setDate(request.date());
                    return a;
                });

            attendance.setStatus(entry.status());
            attendance.setRemarks(entry.remarks());
            Attendance saved = attendanceRepo.save(attendance);
            responses.add(toRecordResponse(saved));
        }

        return responses;
    }

    @Override
    public List<AttendanceRecordResponse> getBySubjectAndDate(Long subjectId, LocalDate date) {
        return attendanceRepo.findBySubjectIdAndDate(subjectId, date).stream()
            .map(this::toRecordResponse)
            .toList();
    }

    @Override
    public List<AttendanceRecordResponse> getStudentRecords(Long studentId, Long subjectId, String currentUsername) {
        Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + studentId));
        checkStudentAccess(student, currentUsername);

        List<Attendance> list;
        if (subjectId != null) {
            list = attendanceRepo.findByStudentIdAndSubjectIdOrderByDateDesc(studentId, subjectId);
        } else {
            list = attendanceRepo.findByStudentIdOrderByDateDesc(studentId);
        }
        return list.stream().map(this::toRecordResponse).toList();
    }

    @Override
    public AttendanceSummaryResponse getStudentSummary(Long studentId, String currentUsername) {
        Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + studentId));
        checkStudentAccess(student, currentUsername);

        return computeSummary(student);
    }

    @Override
    public AttendanceSummaryResponse getMySummary(String currentUsername) {
        User user = userRepo.findByEmail(currentUsername)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + currentUsername));
        Student student = studentRepo.findByUserId(user.getId())
            .orElseGet(() -> studentRepo.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No student profile linked to user " + currentUsername)));

        return computeSummary(student);
    }

    private void checkStudentAccess(Student student, String currentUsername) {
        if (currentUsername == null) return;
        User user = userRepo.findByEmail(currentUsername).orElse(null);
        if (user != null && "STUDENT".equalsIgnoreCase(user.getRole())) {
            boolean matchesId = student.getUser() != null && student.getUser().getId().equals(user.getId());
            boolean matchesEmail = student.getEmail().equalsIgnoreCase(user.getEmail());
            if (!matchesId && !matchesEmail) {
                throw new AccessDeniedException("Access denied: You can only view your own attendance");
            }
        }
    }

    private AttendanceSummaryResponse computeSummary(Student student) {
        List<Attendance> records = attendanceRepo.findByStudentIdOrderByDateDesc(student.getId());

        long totalClasses = records.size();
        long attendedClasses = records.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        double overallPercentage = totalClasses == 0 ? 100.0 : round(attendedClasses * 100.0 / totalClasses);
        boolean overallBelowThreshold = overallPercentage < threshold;

        // Group by Subject
        Map<Subject, List<Attendance>> bySubject = records.stream()
            .collect(Collectors.groupingBy(Attendance::getSubject));

        List<SubjectAttendanceSummary> subjectSummaries = new ArrayList<>();
        for (Map.Entry<Subject, List<Attendance>> entry : bySubject.entrySet()) {
            Subject subj = entry.getKey();
            List<Attendance> subRecords = entry.getValue();
            long subTotal = subRecords.size();
            long subAttended = subRecords.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
            double subPct = subTotal == 0 ? 100.0 : round(subAttended * 100.0 / subTotal);
            subjectSummaries.add(new SubjectAttendanceSummary(
                subj.getId(),
                subj.getCode(),
                subj.getName(),
                subTotal,
                subAttended,
                subPct,
                subPct < threshold
            ));
        }

        // Group by Month (sorted chronologically)
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, List<Attendance>> byMonth = records.stream()
            .collect(Collectors.groupingBy(a -> a.getDate().format(monthFmt), TreeMap::new, Collectors.toList()));

        List<MonthlyAttendanceSummary> monthlySummaries = new ArrayList<>();
        for (Map.Entry<String, List<Attendance>> entry : byMonth.entrySet()) {
            List<Attendance> mRecords = entry.getValue();
            long mTotal = mRecords.size();
            long mAttended = mRecords.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
            double mPct = mTotal == 0 ? 100.0 : round(mAttended * 100.0 / mTotal);
            monthlySummaries.add(new MonthlyAttendanceSummary(
                entry.getKey(),
                mTotal,
                mAttended,
                mPct
            ));
        }

        return new AttendanceSummaryResponse(
            student.getId(),
            student.getFirstName() + " " + student.getLastName(),
            student.getRollNumber(),
            totalClasses,
            attendedClasses,
            overallPercentage,
            overallBelowThreshold,
            threshold,
            subjectSummaries,
            monthlySummaries
        );
    }

    private double round(double val) {
        return Math.round(val * 10.0) / 10.0;
    }

    private AttendanceRecordResponse toRecordResponse(Attendance a) {
        return new AttendanceRecordResponse(
            a.getId(),
            a.getStudent().getId(),
            a.getStudent().getRollNumber(),
            a.getStudent().getFirstName() + " " + a.getStudent().getLastName(),
            a.getSubject().getId(),
            a.getSubject().getCode(),
            a.getSubject().getName(),
            a.getDate(),
            a.getStatus(),
            a.getRemarks()
        );
    }
}
