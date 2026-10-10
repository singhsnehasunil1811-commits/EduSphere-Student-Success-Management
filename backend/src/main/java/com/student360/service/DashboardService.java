package com.student360.service;

import com.student360.dto.NoticeResponse;
import com.student360.entity.Marks;
import com.student360.repository.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final StudentRepository students;
    private final DepartmentRepository departments;
    private final TeacherRepository teachers;
    private final CourseRepository courses;
    private final AttendanceRepository attendance;
    private final MarksRepository marks;
    private final FeeRepository fees;
    private final NoticeService noticeService;

    public DashboardService(
        StudentRepository students,
        DepartmentRepository departments,
        TeacherRepository teachers,
        CourseRepository courses,
        AttendanceRepository attendance,
        MarksRepository marks,
        FeeRepository fees,
        NoticeService noticeService
    ) {
        this.students = students;
        this.departments = departments;
        this.teachers = teachers;
        this.courses = courses;
        this.attendance = attendance;
        this.marks = marks;
        this.fees = fees;
        this.noticeService = noticeService;
    }

    public Map<String, Object> stats() {
        Map<String, Object> m = new LinkedHashMap<>();

        // Core counts
        long totalStudents = students.count();
        long activeStudents = students.countByStatus("ACTIVE");
        long totalTeachers = teachers.count();
        long totalCourses = courses.count();
        long totalDepartments = departments.count();

        m.put("totalStudents", totalStudents);
        m.put("activeStudents", activeStudents);
        m.put("totalTeachers", totalTeachers);
        m.put("totalCourses", totalCourses);
        m.put("totalDepartments", totalDepartments);

        // Average attendance
        long totalAttRecords = attendance.countAllAttendance();
        long presentAttRecords = attendance.countAllPresent();
        double avgAttendance = totalAttRecords > 0
            ? Math.round(presentAttRecords * 1000.0 / totalAttRecords) / 10.0
            : 0.0;
        m.put("averageAttendance", avgAttendance);

        // Attendance overview
        Map<String, Object> attOverview = new LinkedHashMap<>();
        attOverview.put("totalClassesMarked", totalAttRecords);
        attOverview.put("presentCount", presentAttRecords);
        attOverview.put("absentCount", Math.max(0, totalAttRecords - presentAttRecords));
        attOverview.put("averagePercentage", avgAttendance);
        m.put("attendanceOverview", attOverview);

        // Marks & Performance
        List<Marks> allMarks = marks.findAll();
        double avgMarks = 0.0;
        Map<String, Integer> gradeDist = new LinkedHashMap<>();
        gradeDist.put("A+", 0);
        gradeDist.put("A", 0);
        gradeDist.put("B+", 0);
        gradeDist.put("B", 0);
        gradeDist.put("C", 0);
        gradeDist.put("D", 0);
        gradeDist.put("F", 0);

        if (!allMarks.isEmpty()) {
            double totalScoreSum = 0;
            for (Marks mark : allMarks) {
                int total = GradingUtil.computeTotal(
                    mark.getInternal(),
                    mark.getAssignment(),
                    mark.getMidterm(),
                    mark.getEndSem()
                );
                totalScoreSum += total;
                double pct = GradingUtil.computePercentage(total);
                String grade = GradingUtil.computeGrade(pct);
                gradeDist.put(grade, gradeDist.getOrDefault(grade, 0) + 1);
            }
            avgMarks = Math.round((totalScoreSum / allMarks.size()) * 10.0) / 10.0;
        }
        m.put("averageMarks", avgMarks);
        m.put("performanceDistribution", gradeDist);

        // Pending fees
        long pendingFeeCount = fees.countByPaidAmountLessThanTotalFees();
        double pendingFeeAmount = fees.findAll().stream()
            .mapToDouble(f -> Math.max(0.0, f.getTotalFees() - f.getPaidAmount()))
            .sum();
        Map<String, Object> feeStats = new LinkedHashMap<>();
        feeStats.put("pendingCount", pendingFeeCount);
        feeStats.put("pendingAmount", Math.round(pendingFeeAmount * 100.0) / 100.0);
        m.put("pendingFees", feeStats);

        // Students by department
        Map<String, Long> byDept = new LinkedHashMap<>();
        for (Object[] row : students.countByDepartment()) {
            byDept.put((String) row[0], (Long) row[1]);
        }
        m.put("studentsByDepartment", byDept);

        // Recent notices
        List<NoticeResponse> recentNotices = noticeService.getRecent();
        m.put("recentNotices", recentNotices);

        return m;
    }
}
