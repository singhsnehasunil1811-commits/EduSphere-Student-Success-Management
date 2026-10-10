package com.student360.service.impl;

import com.student360.dto.*;
import com.student360.entity.Course;
import com.student360.entity.Department;
import com.student360.entity.Fee;
import com.student360.entity.Student;
import com.student360.entity.User;
import com.student360.exception.DuplicateResourceException;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.AttendanceRepository;
import com.student360.repository.CourseRepository;
import com.student360.repository.DepartmentRepository;
import com.student360.repository.FeeRepository;
import com.student360.repository.StudentRepository;
import com.student360.repository.UserRepository;
import com.student360.service.AttendanceService;
import com.student360.service.MarksService;
import com.student360.service.StudentService;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository students;
    private final DepartmentRepository departments;
    private final CourseRepository courses;
    private final UserRepository users;
    private final FeeRepository feeRepo;
    private final AttendanceRepository attendanceRepo;
    private final AttendanceService attendanceService;
    private final MarksService marksService;

    @Value("${student360.attendance.threshold:75.0}")
    private double attendanceThreshold;

    public StudentServiceImpl(
        StudentRepository students,
        DepartmentRepository departments,
        CourseRepository courses,
        UserRepository users,
        FeeRepository feeRepo,
        AttendanceRepository attendanceRepo,
        @Lazy AttendanceService attendanceService,
        @Lazy MarksService marksService
    ) {
        this.students = students;
        this.departments = departments;
        this.courses = courses;
        this.users = users;
        this.feeRepo = feeRepo;
        this.attendanceRepo = attendanceRepo;
        this.attendanceService = attendanceService;
        this.marksService = marksService;
    }

    @Override
    public List<StudentResponse> search(String q) {
        List<Student> list = (q == null || q.isBlank()) ? students.findAll() : students.search(q.trim());
        return list.stream().map(this::toResponse).toList();
    }

    @Override
    public List<StudentResponse> getAll() {
        return students.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<StudentResponse> getByDepartment(Long departmentId) {
        return students.findByDepartmentId(departmentId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<StudentResponse> getByCourse(Long courseId) {
        return students.findByCourseId(courseId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<StudentResponse> getByCourseAndSemester(Long courseId, int semester) {
        return students.findByCourseIdAndSemester(courseId, semester).stream().map(this::toResponse).toList();
    }

    @Override
    public PageResponse<StudentResponse> getFilteredStudents(
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
    ) {
        List<Student> all = (q == null || q.isBlank()) ? students.findAll() : students.search(q.trim());

        List<Student> filtered = all.stream()
            .filter(s -> departmentId == null || s.getDepartment().getId().equals(departmentId))
            .filter(s -> courseId == null || (s.getCourse() != null && s.getCourse().getId().equals(courseId)))
            .filter(s -> semester == null || s.getSemester() == semester)
            .filter(s -> year == null || s.getYear() == year)
            .filter(s -> status == null || status.isBlank() || s.getStatus().equalsIgnoreCase(status.trim()))
            .sorted((a, b) -> {
                int cmp = 0;
                String field = (sortBy == null) ? "id" : sortBy.toLowerCase();
                switch (field) {
                    case "rollnumber", "roll" -> cmp = a.getRollNumber().compareToIgnoreCase(b.getRollNumber());
                    case "firstname", "name" -> cmp = a.getFirstName().compareToIgnoreCase(b.getFirstName());
                    case "lastname" -> cmp = a.getLastName().compareToIgnoreCase(b.getLastName());
                    case "email" -> cmp = a.getEmail().compareToIgnoreCase(b.getEmail());
                    case "year" -> cmp = Integer.compare(a.getYear(), b.getYear());
                    case "semester", "sem" -> cmp = Integer.compare(a.getSemester(), b.getSemester());
                    case "status" -> cmp = a.getStatus().compareToIgnoreCase(b.getStatus());
                    default -> cmp = Long.compare(a.getId(), b.getId());
                }
                return "desc".equalsIgnoreCase(sortDir) ? -cmp : cmp;
            })
            .toList();

        int totalElements = filtered.size();
        int pageSize = size <= 0 ? 10 : size;
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        int pageNumber = Math.max(0, page);

        int fromIndex = Math.min(pageNumber * pageSize, totalElements);
        int toIndex = Math.min(fromIndex + pageSize, totalElements);
        List<StudentResponse> pageContent = filtered.subList(fromIndex, toIndex).stream()
            .map(this::toResponse)
            .toList();

        return new PageResponse<>(
            pageContent,
            pageNumber,
            pageSize,
            totalElements,
            totalPages,
            pageNumber == 0,
            pageNumber >= totalPages - 1
        );
    }

    @Override
    public StudentResponse get(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    public Student getEntity(Long id) {
        return students.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
    }

    @Override
    public Student getEntityByUserId(Long userId) {
        return students.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Student record not linked to user id " + userId));
    }

    @Override
    public Student getEntityByUserEmail(String email) {
        return students.findByUserEmail(email)
            .orElseGet(() -> students.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student record not found for email " + email)));
    }

    @Override
    public StudentProfileResponse getStudentProfile(Long id, String currentUsername) {
        Student student = getEntity(id);
        checkStudentAccess(student, currentUsername);
        return buildProfile(student, currentUsername);
    }

    @Override
    public StudentProfileResponse getMyProfile(String currentUsername) {
        Student student = getEntityByUserEmail(currentUsername);
        return buildProfile(student, currentUsername);
    }

    private StudentProfileResponse buildProfile(Student s, String currentUsername) {
        // Attendance
        AttendanceSummaryResponse att = attendanceService.getStudentSummary(s.getId(), currentUsername);
        double attPct = att.overallPercentage();
        boolean belowAtt = att.belowThreshold();

        // Marks & CGPA
        StudentGpaResponse gpa = marksService.getStudentGpa(s.getId(), currentUsername);
        double cgpa = gpa.cgpa();
        List<MarksResponse> marksList = marksService.getByStudent(s.getId(), currentUsername);
        double avgMarks = marksList.isEmpty() ? 0.0 :
            Math.round(marksList.stream().mapToDouble(MarksResponse::percentage).average().orElse(0.0) * 10.0) / 10.0;

        // Fees
        List<Fee> fees = feeRepo.findByStudentId(s.getId());
        double totalFees = fees.stream().mapToDouble(Fee::getTotalFees).sum();
        double paidFees = fees.stream().mapToDouble(Fee::getPaidAmount).sum();
        double remainingFees = Math.max(0.0, totalFees - paidFees);
        String feeStatus = fees.isEmpty() ? "NO_RECORD" : (remainingFees == 0 ? "PAID" : (paidFees > 0 ? "PARTIALLY_PAID" : "PENDING"));

        // Recent Activity
        List<Map<String, Object>> activity = new ArrayList<>();
        if (!marksList.isEmpty()) {
            MarksResponse m = marksList.get(0);
            activity.add(Map.of("type", "MARKS", "description", "Score updated in " + m.subjectName() + ": " + m.grade() + " (" + m.total() + "/100)"));
        }
        if (att.totalClasses() > 0) {
            activity.add(Map.of("type", "ATTENDANCE", "description", "Current attendance is " + attPct + "% across " + att.totalClasses() + " lectures"));
        }
        if (!fees.isEmpty()) {
            activity.add(Map.of("type", "FEES", "description", "Fee status is " + feeStatus + " (Remaining: ₹" + remainingFees + ")"));
        }

        return new StudentProfileResponse(
            s.getId(),
            s.getStudentCode(),
            s.getRollNumber(),
            s.getFirstName(),
            s.getLastName(),
            s.getEmail(),
            s.getPhone(),
            s.getStatus(),
            s.getAdmissionDate(),
            s.getYear(),
            s.getSemester(),
            s.getDepartment().getId(),
            s.getDepartment().getName(),
            s.getCourse() != null ? s.getCourse().getId() : null,
            s.getCourse() != null ? s.getCourse().getName() : null,
            attPct,
            belowAtt,
            avgMarks,
            cgpa,
            feeStatus,
            Math.round(totalFees * 100.0) / 100.0,
            Math.round(paidFees * 100.0) / 100.0,
            Math.round(remainingFees * 100.0) / 100.0,
            marksList,
            activity
        );
    }

    private void checkStudentAccess(Student student, String currentUsername) {
        if (currentUsername == null) return;
        User user = users.findByEmail(currentUsername).orElse(null);
        if (user != null && "STUDENT".equalsIgnoreCase(user.getRole())) {
            boolean matchesId = student.getUser() != null && student.getUser().getId().equals(user.getId());
            boolean matchesEmail = student.getEmail().equalsIgnoreCase(user.getEmail());
            if (!matchesId && !matchesEmail) {
                throw new AccessDeniedException("Access denied: You can only view your own student profile");
            }
        }
    }

    @Override
    @Transactional
    public StudentResponse create(StudentRequest r) {
        checkUnique(r, null);
        Student s = apply(new Student(), r);
        return toResponse(students.save(s));
    }

    @Override
    @Transactional
    public StudentResponse update(Long id, StudentRequest r) {
        Student s = getEntity(id);
        checkUnique(r, id);
        return toResponse(students.save(apply(s, r)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Student s = getEntity(id);
        students.delete(s);
    }

    private void checkUnique(StudentRequest r, Long selfId) {
        students.findByRollNumber(r.rollNumber().trim().toUpperCase())
            .filter(o -> !o.getId().equals(selfId))
            .ifPresent(o -> {
                throw new DuplicateResourceException("Roll number already exists");
            });

        students.findByStudentCode(r.studentCode().trim().toUpperCase())
            .filter(o -> !o.getId().equals(selfId))
            .ifPresent(o -> {
                throw new DuplicateResourceException("Student ID already exists");
            });

        students.findByEmail(r.email().trim().toLowerCase())
            .filter(o -> !o.getId().equals(selfId))
            .ifPresent(o -> {
                throw new DuplicateResourceException("Email already exists");
            });
    }

    private Student apply(Student s, StudentRequest r) {
        Department d = departments.findById(r.departmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + r.departmentId()));

        Course c = null;
        if (r.courseId() != null) {
            c = courses.findById(r.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + r.courseId()));
        }

        s.setStudentCode(r.studentCode().trim().toUpperCase());
        s.setRollNumber(r.rollNumber().trim().toUpperCase());
        s.setFirstName(r.firstName().trim());
        s.setLastName(r.lastName().trim());
        s.setEmail(r.email().trim().toLowerCase());
        s.setPhone(r.phone() != null ? r.phone().trim() : null);
        s.setDepartment(d);
        s.setCourse(c);
        s.setYear(r.year());
        s.setSemester(r.semester());
        if (r.status() != null && !r.status().isBlank()) {
            s.setStatus(r.status().trim().toUpperCase());
        }

        if (r.userId() != null) {
            User u = users.findById(r.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + r.userId()));
            s.setUser(u);
        }

        return s;
    }

    @Override
    public StudentResponse toResponse(Student s) {
        return new StudentResponse(
            s.getId(),
            s.getStudentCode(),
            s.getRollNumber(),
            s.getFirstName(),
            s.getLastName(),
            s.getEmail(),
            s.getPhone(),
            s.getDepartment().getId(),
            s.getDepartment().getName(),
            s.getCourse() != null ? s.getCourse().getId() : null,
            s.getCourse() != null ? s.getCourse().getName() : null,
            s.getYear(),
            s.getSemester(),
            s.getStatus(),
            s.getAdmissionDate(),
            s.getUser() != null ? s.getUser().getId() : null
        );
    }
}
