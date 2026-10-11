package com.student360.config;

import com.student360.entity.*;
import com.student360.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Master DataSeeder populating realistic demo data for all modules:
 * Users, Departments, Courses, Teachers, Subjects, Students,
 * Attendance, Marks, Assignments & Submissions, Fees, Notices, Timetable.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final DepartmentRepository depts;
    private final CourseRepository courses;
    private final TeacherRepository teachers;
    private final SubjectRepository subjects;
    private final StudentRepository students;
    private final AttendanceRepository attendance;
    private final MarksRepository marks;
    private final AssignmentRepository assignments;
    private final AssignmentSubmissionRepository submissions;
    private final FeeRepository fees;
    private final NoticeRepository notices;
    private final TimetableRepository timetables;
    private final AuditLogRepository auditLogs;
    private final PasswordEncoder enc;

    public DataSeeder(
        UserRepository u,
        DepartmentRepository d,
        CourseRepository c,
        TeacherRepository t,
        SubjectRepository sub,
        StudentRepository s,
        AttendanceRepository att,
        MarksRepository m,
        AssignmentRepository a,
        AssignmentSubmissionRepository subms,
        FeeRepository f,
        NoticeRepository n,
        TimetableRepository tt,
        AuditLogRepository al,
        PasswordEncoder e
    ) {
        this.users = u;
        this.depts = d;
        this.courses = c;
        this.teachers = t;
        this.subjects = sub;
        this.students = s;
        this.attendance = att;
        this.marks = m;
        this.assignments = a;
        this.submissions = subms;
        this.fees = f;
        this.notices = n;
        this.timetables = tt;
        this.auditLogs = al;
        this.enc = e;
    }

    @Override
    public void run(String... args) {
        if (users.count() > 0) return;

        // 1. Users
        User adminUser = addUser("admin@student360.com", "Admin User", "admin123", "ADMIN");
        User teacherUser1 = addUser("teacher@student360.com", "Priya Sharma", "teacher123", "TEACHER");
        User teacherUser2 = addUser("rajesh.gupta@student360.com", "Dr. Rajesh Gupta", "teacher123", "TEACHER");
        User teacherUser3 = addUser("sunita.d@student360.com", "Sunita Deshmukh", "teacher123", "TEACHER");
        User teacherUser4 = addUser("amit.patel@student360.com", "Amit Patel", "teacher123", "TEACHER");
        User teacherUser5 = addUser("kavita.nair@student360.com", "Kavita Nair", "teacher123", "TEACHER");

        User studentUser1 = addUser("student@student360.com", "Rahul Verma", "student123", "STUDENT");

        // 2. Departments
        Department cseDept = addDept("Computer Science & Engineering", "CSE", "Dr. Mehta", "Department of Computer Science");
        Department eceDept = addDept("Electronics & Communication", "ECE", "Dr. Iyer", "Department of Electronics");
        Department itDept = addDept("Information Technology", "IT", "Dr. Joshi", "Department of IT");
        Department mechDept = addDept("Mechanical Engineering", "MECH", "Dr. Khan", "Department of Mechanical Engineering");
        Department civilDept = addDept("Civil Engineering", "CIVIL", "Dr. Rao", "Department of Civil Engineering");

        // 3. Courses
        Course btechCse = addCourse("CS101", "B.Tech Computer Science & Engineering", 4, cseDept);
        Course btechIt = addCourse("IT101", "B.Tech Information Technology", 4, itDept);
        Course btechEce = addCourse("EC101", "B.Tech Electronics & Communication", 4, eceDept);
        Course btechMech = addCourse("ME101", "B.Tech Mechanical Engineering", 4, mechDept);
        Course btechCivil = addCourse("CE101", "B.Tech Civil Engineering", 4, civilDept);

        // 4. Teachers
        Teacher t1 = addTeacher("TCH001", "Priya", "Sharma", "teacher@student360.com", "9876500001", "Associate Professor", "Ph.D Computer Science", cseDept, teacherUser1);
        Teacher t2 = addTeacher("TCH002", "Rajesh", "Gupta", "rajesh.gupta@student360.com", "9876500002", "Professor & Dean", "Ph.D Electronics", eceDept, teacherUser2);
        Teacher t3 = addTeacher("TCH003", "Sunita", "Deshmukh", "sunita.d@student360.com", "9876500003", "Assistant Professor", "M.Tech Software Systems", itDept, teacherUser3);
        Teacher t4 = addTeacher("TCH004", "Amit", "Patel", "amit.patel@student360.com", "9876500004", "Associate Professor", "Ph.D Thermal Engineering", mechDept, teacherUser4);
        Teacher t5 = addTeacher("TCH005", "Kavita", "Nair", "kavita.nair@student360.com", "9876500005", "Assistant Professor", "M.Tech Structural Engineering", civilDept, teacherUser5);

        // 5. Subjects
        Subject s1 = addSubject("CS301", "Data Structures & Algorithms", 4, 3, btechCse, t1);
        Subject s2 = addSubject("CS302", "Database Management Systems", 4, 3, btechCse, t1);
        Subject s3 = addSubject("CS401", "Operating Systems", 4, 4, btechCse, t1);
        Subject s4 = addSubject("CS402", "Computer Networks", 3, 4, btechCse, t1);
        Subject s5 = addSubject("EC201", "Digital Electronics", 4, 2, btechEce, t2);
        Subject s6 = addSubject("EC301", "Signals and Systems", 4, 3, btechEce, t2);
        Subject s7 = addSubject("IT301", "Web Technologies", 3, 3, btechIt, t3);
        Subject s8 = addSubject("IT401", "Cloud Computing", 3, 4, btechIt, t3);
        Subject s9 = addSubject("ME201", "Thermodynamics", 4, 2, btechMech, t4);
        Subject s10 = addSubject("CE301", "Structural Analysis", 4, 3, btechCivil, t5);

        // 6. Students (20)
        String[] first = {"Rahul", "Diya", "Rohan", "Sneha", "Kabir", "Ananya", "Vihaan", "Ishita", "Arjun", "Meera",
                         "Siddharth", "Tanvi", "Aditya", "Pooja", "Varun", "Rhea", "Manish", "Kritika", "Nikhil", "Simran"};
        String[] last = {"Verma", "Singh", "Nair", "Desai", "Patel", "Iyer", "Sharma", "Joshi", "Kulkarni", "Chopra",
                        "Mehta", "Reddy", "Bose", "Gupta", "Malhotra", "Kapoor", "Pillai", "Das", "Bhat", "Rao"};

        Department[] deptList = {cseDept, itDept, eceDept, mechDept, civilDept};
        Course[] courseList = {btechCse, btechIt, btechEce, btechMech, btechCivil};

        List<Student> savedStudents = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Student s = new Student();
            s.setStudentCode("STU" + String.format("%03d", i));
            s.setRollNumber("R" + (1000 + i));
            s.setFirstName(first[i - 1]);
            s.setLastName(last[i - 1]);

            if (i == 1) {
                s.setEmail("student@student360.com");
                s.setUser(studentUser1);
                s.setDepartment(cseDept);
                s.setCourse(btechCse);
                s.setYear(3);
                s.setSemester(5);
            } else {
                s.setEmail("student" + i + "@college.edu");
                int idx = (i - 1) % 5;
                s.setDepartment(deptList[idx]);
                s.setCourse(courseList[idx]);
                s.setYear(1 + ((i - 1) % 4));
                s.setSemester(1 + ((i - 1) % 8));

                User stuUser = addUser(s.getEmail(), s.getFirstName() + " " + s.getLastName(), "student123", "STUDENT");
                s.setUser(stuUser);
            }

            s.setPhone("98765432" + String.format("%02d", i));
            s.setStatus(i % 9 == 0 ? "INACTIVE" : "ACTIVE");
            s.setAdmissionDate(LocalDate.now().minusMonths(6L * s.getSemester()));
            savedStudents.add(students.save(s));
        }

        // 7. Realistic Attendance Records
        // Dates across last 4 weeks (15 class days)
        LocalDate baseDate = LocalDate.now();
        List<LocalDate> classDates = new ArrayList<>();
        for (int d = 25; d >= 1; d--) {
            LocalDate dt = baseDate.minusDays(d);
            if (dt.getDayOfWeek().getValue() <= 5) { // Monday-Friday
                classDates.add(dt);
                if (classDates.size() >= 15) break;
            }
        }

        // Seed for Student 1 (Rahul Verma) in CS301, CS302, CS401
        Student rahul = savedStudents.get(0);
        Subject[] rahulSubjects = {s1, s2, s3, s4};
        for (Subject sub : rahulSubjects) {
            for (int idx = 0; idx < classDates.size(); idx++) {
                Attendance att = new Attendance();
                att.setStudent(rahul);
                att.setSubject(sub);
                att.setDate(classDates.get(idx));
                // ~87% attendance (13 present, 2 absent)
                AttendanceStatus st = (idx == 3 || idx == 11) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
                att.setStatus(st);
                att.setRemarks(st == AttendanceStatus.ABSENT ? "Medical" : "Regular");
                attendance.save(att);
            }
        }

        // Seed for Student 2 (Diya Singh) with low attendance (~60%, below 75% threshold)
        Student diya = savedStudents.get(1);
        for (int idx = 0; idx < classDates.size(); idx++) {
            Attendance att = new Attendance();
            att.setStudent(diya);
            att.setSubject(s7); // IT301
            att.setDate(classDates.get(idx));
            AttendanceStatus st = (idx % 3 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
            att.setStatus(st);
            att.setRemarks(st == AttendanceStatus.ABSENT ? "Absent" : "Present");
            attendance.save(att);
        }

        // Seed sample attendance for other students
        for (int i = 2; i < 10; i++) {
            Student st = savedStudents.get(i);
            Subject sub = (i % 2 == 0) ? s1 : s2;
            for (int idx = 0; idx < 10; idx++) {
                Attendance att = new Attendance();
                att.setStudent(st);
                att.setSubject(sub);
                att.setDate(classDates.get(idx));
                AttendanceStatus status = (idx % 5 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
                att.setStatus(status);
                att.setRemarks("Class attendance");
                attendance.save(att);
            }
        }

        // 8. Marks
        // Rahul Verma's marks
        addMarks(rahul, s1, 18, 17, 26, 25); // Total: 86, Grade: A
        addMarks(rahul, s2, 19, 18, 27, 28); // Total: 92, Grade: A+
        addMarks(rahul, s3, 16, 17, 24, 23); // Total: 80, Grade: A
        addMarks(rahul, s4, 15, 16, 22, 22); // Total: 75, Grade: B+

        // Diya Singh's marks
        addMarks(diya, s7, 14, 15, 20, 19); // Total: 68, Grade: B

        // Other students' marks
        for (int i = 2; i < 15; i++) {
            Student st = savedStudents.get(i);
            Subject sub = (i % 2 == 0) ? s1 : s2;
            int internal = 12 + (i % 8);
            int assignment = 13 + (i % 7);
            int midterm = 18 + (i % 12);
            int endSem = 18 + (i % 12);
            addMarks(st, sub, internal, assignment, midterm, endSem);
        }

        // 9. Assignments & Submissions
        Assignment a1 = addAssignment("DSA Problem Set: Binary Trees & Graphs",
            "Implement AVL Tree insertion, rotation and BFS/DFS graph traversals in Java with comprehensive test cases.",
            s1, LocalDate.now().plusDays(10), 100, t1);

        Assignment a2 = addAssignment("DBMS Project: Relational Schema & Indexing",
            "Design normalized schema (3NF/BCNF) and demonstrate query optimization using B+Tree indexing.",
            s2, LocalDate.now().plusDays(14), 50, t1);

        Assignment a3 = addAssignment("OS Lab: Mutexes & Semaphore Synchronization",
            "Implement reader-writer and producer-consumer problems using POSIX threads and binary semaphores.",
            s3, LocalDate.now().minusDays(2), 50, t1);

        Assignment a4 = addAssignment("Web Technologies: Single-Page Dashboard",
            "Build an interactive SPA using vanilla JavaScript and CSS glassmorphism.",
            s7, LocalDate.now().plusDays(7), 50, t3);

        // Submissions
        addSubmission(rahul, a1, LocalDateTime.now().minusDays(1), "SUBMITTED");
        addSubmission(rahul, a2, LocalDateTime.now().minusDays(3), "SUBMITTED");
        addSubmission(rahul, a3, LocalDateTime.now().minusDays(1), "LATE"); // past deadline

        addSubmission(diya, a4, LocalDateTime.now().minusHours(5), "SUBMITTED");

        // 10. Fees
        addFee(rahul, "2025-2026", 85000.0, 85000.0, LocalDate.now().plusMonths(3), "Tuition & Lab fees paid in full");
        addFee(diya, "2025-2026", 85000.0, 50000.0, LocalDate.now().plusMonths(1), "First installment paid; balance due");
        for (int i = 2; i < 10; i++) {
            Student st = savedStudents.get(i);
            double total = 80000.0;
            double paid = (i % 3 == 0) ? 0.0 : (i % 2 == 0 ? 80000.0 : 40000.0);
            addFee(st, "2025-2026", total, paid, LocalDate.now().plusMonths(2), "Annual college fee");
        }

        // 11. Notices
        addNotice(
            "Mid-Semester Examination Schedule Announced",
            "The Mid-Semester examinations for all B.Tech branches will commence from the 20th of this month. Detailed date-sheet is available on the departmental notice boards.",
            NoticePriority.URGENT,
            LocalDate.now().minusDays(1),
            TargetAudience.ALL,
            adminUser
        );

        addNotice(
            "Campus Recruitment Drive: Top Tech Companies Visiting",
            "Google, Microsoft and Amazon recruitment registration portal is now open for pre-final and final year students. Ensure your academic records and CGPA are up-to-date.",
            NoticePriority.IMPORTANT,
            LocalDate.now().minusDays(3),
            TargetAudience.STUDENTS,
            teacherUser1
        );

        addNotice(
            "Faculty Development Programme on Cloud & AI",
            "A 3-day workshop on modern cloud architectures and applied machine learning will be held next week in Seminar Hall A.",
            NoticePriority.NORMAL,
            LocalDate.now().minusDays(5),
            TargetAudience.TEACHERS,
            adminUser
        );

        addNotice(
            "Annual Technical Symposium: INNOVIZ 2026",
            "Call for project presentations, coding hackathons, and robotics challenges. Prize pool worth INR 2,00,000.",
            NoticePriority.IMPORTANT,
            LocalDate.now().minusDays(7),
            TargetAudience.ALL,
            teacherUser2
        );

        addNotice(
            "Library System Maintenance Downtime",
            "The central digital library server will undergo scheduled maintenance this Sunday from 10:00 PM to 4:00 AM.",
            NoticePriority.NORMAL,
            LocalDate.now().minusDays(10),
            TargetAudience.ALL,
            adminUser
        );

        // 12. Timetable (Weekly Schedule)
        addTimetable("MONDAY", LocalTime.of(9, 0), LocalTime.of(10, 0), s1, t1, "Room 301");
        addTimetable("MONDAY", LocalTime.of(10, 15), LocalTime.of(11, 15), s2, t1, "Lab 2");
        addTimetable("MONDAY", LocalTime.of(11, 30), LocalTime.of(12, 30), s3, t1, "Room 301");
        addTimetable("TUESDAY", LocalTime.of(9, 0), LocalTime.of(10, 0), s4, t1, "Room 302");
        addTimetable("TUESDAY", LocalTime.of(10, 15), LocalTime.of(11, 15), s1, t1, "Room 301");
        addTimetable("WEDNESDAY", LocalTime.of(9, 0), LocalTime.of(10, 0), s2, t1, "Room 301");
        addTimetable("WEDNESDAY", LocalTime.of(10, 15), LocalTime.of(11, 15), s3, t1, "Lab 1");
        addTimetable("THURSDAY", LocalTime.of(9, 0), LocalTime.of(10, 0), s4, t1, "Room 302");
        addTimetable("THURSDAY", LocalTime.of(10, 15), LocalTime.of(11, 15), s1, t1, "Room 301");
        addTimetable("FRIDAY", LocalTime.of(9, 0), LocalTime.of(10, 0), s2, t1, "Lab 2");
        addTimetable("FRIDAY", LocalTime.of(10, 15), LocalTime.of(11, 15), s7, t3, "Room 401");

        // 13. Institutional Audit Logs
        auditLogs.save(new AuditLog("Student", rahul.getId(), "ENROLLMENT", "admin@student360.com", "ROLE_ADMIN", "Student Rahul Verma officially enrolled in B.Tech CSE"));
        auditLogs.save(new AuditLog("Marks", s1.getId(), "GRADE_ENTRY", "teacher@student360.com", "ROLE_TEACHER", "End-Semester marks recorded for Data Structures (CS301)"));
        auditLogs.save(new AuditLog("Attendance", s2.getId(), "BULK_ATTENDANCE", "teacher@student360.com", "ROLE_TEACHER", "Class roster attendance marked for Database Systems (CS302)"));
        auditLogs.save(new AuditLog("Fee", rahul.getId(), "PAYMENT_RECEIPT", "admin@student360.com", "ROLE_ADMIN", "Full payment of ₹85,000 received and cleared for academic year 2025-2026"));
    }

    private User addUser(String email, String name, String pw, String role) {
        User u = new User();
        u.setEmail(email.toLowerCase().trim());
        u.setFullName(name.trim());
        u.setPassword(enc.encode(pw));
        u.setRole(role);
        u.setEnabled(true);
        return users.save(u);
    }

    private Department addDept(String name, String code, String hod, String desc) {
        Department d = new Department();
        d.setName(name);
        d.setCode(code);
        d.setHod(hod);
        d.setDescription(desc);
        return depts.save(d);
    }

    private Course addCourse(String code, String name, int duration, Department dept) {
        Course c = new Course();
        c.setCourseCode(code);
        c.setName(name);
        c.setDurationYears(duration);
        c.setDepartment(dept);
        return courses.save(c);
    }

    private Teacher addTeacher(String code, String fn, String ln, String email, String phone, String desig, String qual, Department d, User u) {
        Teacher t = new Teacher();
        t.setTeacherCode(code);
        t.setFirstName(fn);
        t.setLastName(ln);
        t.setEmail(email);
        t.setPhone(phone);
        t.setDesignation(desig);
        t.setQualification(qual);
        t.setDepartment(d);
        t.setUser(u);
        return teachers.save(t);
    }

    private Subject addSubject(String code, String name, int credits, int sem, Course c, Teacher t) {
        Subject s = new Subject();
        s.setCode(code);
        s.setName(name);
        s.setCredits(credits);
        s.setSemester(sem);
        s.setCourse(c);
        s.setTeacher(t);
        return subjects.save(s);
    }

    private void addMarks(Student st, Subject sub, int in, int as, int mid, int end) {
        Marks m = new Marks();
        m.setStudent(st);
        m.setSubject(sub);
        m.setInternal(in);
        m.setAssignment(as);
        m.setMidterm(mid);
        m.setEndSem(end);
        marks.save(m);
    }

    private Assignment addAssignment(String title, String desc, Subject sub, LocalDate dl, int maxM, Teacher cr) {
        Assignment a = new Assignment();
        a.setTitle(title);
        a.setDescription(desc);
        a.setSubject(sub);
        a.setDeadline(dl);
        a.setMaxMarks(maxM);
        a.setCreatedBy(cr);
        a.setCreatedAt(LocalDateTime.now().minusDays(5));
        return assignments.save(a);
    }

    private void addSubmission(Student st, Assignment a, LocalDateTime subAt, String status) {
        AssignmentSubmission as = new AssignmentSubmission();
        as.setStudent(st);
        as.setAssignment(a);
        as.setSubmittedAt(subAt);
        as.setStatus(status);
        submissions.save(as);
    }

    private void addFee(Student st, String ay, double tot, double paid, LocalDate due, String rem) {
        Fee f = new Fee();
        f.setStudent(st);
        f.setAcademicYear(ay);
        f.setTotalFees(tot);
        f.setPaidAmount(paid);
        f.setDueDate(due);
        f.setRemarks(rem);
        fees.save(f);
    }

    private void addNotice(String title, String content, NoticePriority prio, LocalDate d, TargetAudience aud, User cr) {
        Notice n = new Notice();
        n.setTitle(title);
        n.setContent(content);
        n.setPriority(prio);
        n.setDate(d);
        n.setTargetAudience(aud);
        n.setCreatedBy(cr);
        n.setCreatedAt(LocalDateTime.now().minusDays(2));
        notices.save(n);
    }

    private void addTimetable(String day, LocalTime start, LocalTime end, Subject sub, Teacher t, String room) {
        Timetable tt = new Timetable();
        tt.setDay(day);
        tt.setStartTime(start);
        tt.setEndTime(end);
        tt.setSubject(sub);
        tt.setTeacher(t);
        tt.setRoom(room);
        timetables.save(tt);
    }
}
