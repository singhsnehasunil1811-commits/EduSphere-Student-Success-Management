# STUDENT360 — REST API Specification

Comprehensive documentation for all REST API endpoints implemented across the platform.

---

## 1. Authentication (`/api/auth`)

### 1.1 User Login
- **URL**: `POST /api/auth/login`
- **Role**: Public
- **Request Body**:
  ```json
  {
    "email": "admin@student360.com",
    "password": "admin123",
    "role": "ADMIN"
  }
  ```
- **Response** (200 OK):
  ```json
  {
    "id": 1,
    "email": "admin@student360.com",
    "fullName": "Admin User",
    "role": "ADMIN"
  }
  ```
- **Errors**: `400 Bad Request`, `401 Unauthorized`.

### 1.2 User Logout
- **URL**: `POST /api/auth/logout`
- **Role**: Authenticated
- **Response**: `204 No Content`

### 1.3 Get Current User Session
- **URL**: `GET /api/auth/me`
- **Role**: Authenticated
- **Response** (200 OK): Current user details.

---

## 2. Dashboard & Analytics (`/api/dashboard`)

### 2.1 Campus Intelligence Stats
- **URL**: `GET /api/dashboard/stats`
- **Role**: Authenticated (`ADMIN`, `TEACHER`, `STUDENT`)
- **Response** (200 OK):
  ```json
  {
    "totalStudents": 20,
    "activeStudents": 18,
    "totalTeachers": 5,
    "totalCourses": 5,
    "totalDepartments": 5,
    "averageAttendance": 81.3,
    "averageMarks": 78.8,
    "attendanceOverview": {
      "totalClassesMarked": 140,
      "presentCount": 114,
      "absentCount": 26,
      "averagePercentage": 81.3
    },
    "performanceDistribution": {
      "A+": 4, "A": 6, "B+": 3, "B": 2, "C": 1, "D": 0, "F": 0
    },
    "pendingFees": {
      "pendingCount": 4,
      "pendingAmount": 125000.0
    },
    "studentsByDepartment": {
      "Computer Science & Engineering": 8,
      "Electronics & Communication": 4
    },
    "recentNotices": [...]
  }
  ```

---

## 3. Academic Structure (`Departments, Courses, Subjects, Teachers`)

### 3.1 Departments
- `GET /api/departments` — List departments (`ADMIN`, `TEACHER`, `STUDENT`)
- `GET /api/departments/{id}` — Get department
- `POST /api/departments` — Create department (`ADMIN`)
- `PUT /api/departments/{id}` — Update department (`ADMIN`)
- `DELETE /api/departments/{id}` — Delete department (`ADMIN`)

### 3.2 Courses
- `GET /api/courses` — List courses (`ADMIN`, `TEACHER`, `STUDENT`)
- `GET /api/courses/{id}` — Get course
- `POST /api/courses` — Create course (`ADMIN`)
- `PUT /api/courses/{id}` — Update course (`ADMIN`)
- `DELETE /api/courses/{id}` — Delete course (`ADMIN`)

### 3.3 Teachers
- `GET /api/teachers` — List faculty (`ADMIN`, `TEACHER`, `STUDENT`)
- `GET /api/teachers/{id}` — Get faculty member
- `POST /api/teachers` — Register faculty (`ADMIN`)
- `PUT /api/teachers/{id}` — Update faculty (`ADMIN`)
- `DELETE /api/teachers/{id}` — Remove faculty (`ADMIN`)

### 3.4 Subjects
- `GET /api/subjects` — List subjects (`ADMIN`, `TEACHER`, `STUDENT`)
- `GET /api/subjects/{id}` — Get subject
- `POST /api/subjects` — Add subject (`ADMIN`)
- `PUT /api/subjects/{id}` — Update subject (`ADMIN`)
- `DELETE /api/subjects/{id}` — Delete subject (`ADMIN`)

---

## 4. Student Directory & Profiler (`/api/students`)

### 4.1 Filtered & Paginated Student Search
- **URL**: `GET /api/students/paged?page=0&size=10&sortBy=id&sortDir=asc&q=Rahul&departmentId=1&status=ACTIVE`
- **Role**: `ADMIN`, `TEACHER`
- **Response** (200 OK):
  ```json
  {
    "content": [...],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 20,
    "totalPages": 2,
    "isFirst": true,
    "isLast": false
  }
  ```

### 4.2 Comprehensive 360° Student Profile
- **URL**: `GET /api/students/{id}/profile`
- **Role**: `ADMIN`, `TEACHER`, or the `STUDENT` owner
- **Response** (200 OK):
  ```json
  {
    "id": 1,
    "studentCode": "STU001",
    "rollNumber": "R1001",
    "firstName": "Rahul",
    "lastName": "Verma",
    "email": "student@student360.com",
    "departmentName": "Computer Science & Engineering",
    "courseName": "B.Tech Computer Science & Engineering",
    "attendancePercentage": 86.7,
    "belowAttendanceThreshold": false,
    "averageMarks": 83.3,
    "cgpa": 9.25,
    "feeStatus": "PAID",
    "totalFees": 85000.0,
    "paidFees": 85000.0,
    "remainingFees": 0.0,
    "recentMarks": [...],
    "recentActivity": [...]
  }
  ```

### 4.3 Student Self Profile
- **URL**: `GET /api/students/profile/me`
- **Role**: `STUDENT`
- **Response**: Own student 360° profile.

---

## 5. Attendance Operations (`/api/attendance`)

### 5.1 Bulk Attendance Marking
- **URL**: `POST /api/attendance/bulk`
- **Role**: `ADMIN`, `TEACHER`
- **Request Body**:
  ```json
  {
    "subjectId": 1,
    "date": "2026-10-07",
    "entries": [
      { "studentId": 1, "status": "PRESENT", "remarks": "Regular" },
      { "studentId": 2, "status": "ABSENT", "remarks": "Medical" }
    ]
  }
  ```
- **Response**: `200 OK` List of saved attendance records.

### 5.2 Student Attendance Summary
- **URL**: `GET /api/attendance/student/{id}/summary`
- **Role**: `ADMIN`, `TEACHER`, or student owner
- **Response** (200 OK):
  ```json
  {
    "studentId": 1,
    "totalClasses": 60,
    "attendedClasses": 52,
    "overallPercentage": 86.7,
    "belowThreshold": false,
    "subjectSummaries": [...]
  }
  ```

### 5.3 Own Attendance Summary
- **URL**: `GET /api/attendance/my-summary`
- **Role**: `STUDENT`

---

## 6. Examination Marks & Grading (`/api/marks`)

### 6.1 Create or Update Marks
- **URL**: `POST /api/marks`
- **Role**: `ADMIN`, `TEACHER`
- **Request Body**:
  ```json
  {
    "studentId": 1,
    "subjectId": 1,
    "internal": 18,
    "assignment": 17,
    "midterm": 26,
    "endSem": 25
  }
  ```
- **Response** (200 OK): Total: 86, Percentage: 86.0%, Grade: "A", GradePoints: 9.0.

### 6.2 Student GPA & CGPA
- **URL**: `GET /api/marks/student/{id}/gpa`
- **Role**: `ADMIN`, `TEACHER`, or student owner
- **Response** (200 OK):
  ```json
  {
    "studentId": 1,
    "studentName": "Rahul Verma",
    "studentRoll": "R1001",
    "gpa": 9.25,
    "cgpa": 9.25,
    "semesters": [...]
  }
  ```

### 6.3 Student Own Marks & GPA
- `GET /api/marks/my`
- `GET /api/marks/my/gpa`

---

## 7. Course Assignments (`/api/assignments`)

- `GET /api/assignments` — List all assignments
- `POST /api/assignments` — Post assignment (`ADMIN`, `TEACHER`)
- `POST /api/assignments/{id}/submit` — Submit assignment (`STUDENT`)
- `GET /api/assignments/{id}/submissions` — View submissions (`ADMIN`, `TEACHER`)
- `DELETE /api/assignments/{id}` — Delete assignment (`ADMIN`, `TEACHER`)

---

## 8. Institutional Fees (`/api/fees`)

- `GET /api/fees` — List fee records (`ADMIN`)
- `GET /api/fees/student/{studentId}` — Get student fee statement
- `GET /api/fees/my` — Get own fee statement (`STUDENT`)
- `POST /api/fees/{id}/pay` — Record counter/online payment (`ADMIN` or Student self)

---

## 9. Campus Notices (`/api/notices`)

- `GET /api/notices` — List circular notices
- `GET /api/notices/recent` — Top 5 recent notices
- `POST /api/notices` — Publish announcement (`ADMIN`, `TEACHER`)
- `DELETE /api/notices/{id}` — Delete notice (`ADMIN`, `TEACHER`)

---

## 10. Timetable (`/api/timetable`)

- `GET /api/timetable` — Full weekly schedule
- `POST /api/timetable` — Add timetable slot (`ADMIN`)
- `DELETE /api/timetable/{id}` — Remove timetable slot (`ADMIN`)

---

## 11. User Management (`/api/users`)

- `GET /api/users` — List all system accounts (`ADMIN`)
- `POST /api/users` — Create new account (`ADMIN`)
- `PUT /api/users/{id}/reset-password` — Reset password (`ADMIN`)
- `PUT /api/users/{id}/disable` — Disable login account (`ADMIN`)
- `PUT /api/users/{id}/enable` — Re-enable account (`ADMIN`)
