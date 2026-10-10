# 🎓 EduSphere — Intelligent Student Success Management System

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-Session%20Auth-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

> **Beyond data storage — real-time academic intelligence.**
> EduSphere doesn't just manage student records. It analyzes attendance, marks, assignments, and engagement data to compute a **Student Success Score**, flag at-risk students with an **Early Warning Engine**, generate **personalized recommendations**, and provide faculty with a **Class Health Dashboard** — all powered by pure Java business logic.

---

## 🧠 What Makes EduSphere Unique

| Feature | Description |
|---------|-------------|
| 🎯 **Student Success Score** | Dynamic 0–100 score computed from attendance (30%), academics (35%), assignments (20%), and performance trend (15%). Status: `ON TRACK` / `NEEDS ATTENTION` / `CRITICAL`. |
| ⚠️ **Early Warning Engine** | Rule-based risk analysis engine that detects attendance drops, low marks, overdue assignments, and performance decline. Generates risk level (`LOW` → `CRITICAL`) with recommended interventions. |
| 📊 **Performance Trend Analysis** | Detects whether a student is `IMPROVING`, `STABLE`, or `DECLINING` based on historical marks data. |
| 🧮 **Smart Attendance Calculator** | Calculates exactly how many consecutive classes a student needs to attend to reach the required 75% threshold. |
| 💡 **Personalized Recommendations** | Auto-generated, data-driven suggestions like _"Improve DBMS attendance"_ or _"Focus on subjects with declining marks"_. |
| 🏥 **Faculty Class Health Dashboard** | Bird's-eye view of class health: students on track, needing attention, or critical — with a prioritized intervention list. |
| 🤖 **EduBot Assistant** | Rule-based chatbot that answers questions using the student's actual stored data (e.g., _"Why is my score low?"_, _"How many classes do I need?"_). |
| 🎫 **Support Ticket System** | Students raise academic/support requests; faculty can view, assign, and resolve tickets. |
| 🔔 **Notification Center** | Real-time alerts for low attendance, new assignments, approaching deadlines, and performance warnings. |
| 🔍 **Global Search** | Unified search across students, subjects, assignments, and support tickets. |

---

## 🏗️ System Architecture

```
┌──────────────────────────────────────────────────────────────────────────┐
│                     CLIENT LAYER (Modern Web Browser)                    │
│   • Glassmorphic SPA (HTML5 / CSS3 / Vanilla JavaScript)                │
│   • 3D Tilt Cards, Animated Counters, Canvas Charts, Progress Rings     │
│   • Role-Aware Navigation: ADMIN, TEACHER, STUDENT                      │
└─────────────────────────────────┬────────────────────────────────────────┘
                                  │ HTTP / REST / JSON (Session Auth)
                                  ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                    SPRING BOOT 3 BACKEND MONOLITH                        │
│                                                                          │
│  ┌─ Controller Layer ──────────────────────────────────────────────────┐ │
│  │  REST Controllers with Jakarta Bean Validation                      │ │
│  │  Global Exception Handler (@RestControllerAdvice)                   │ │
│  └─────────────────────────────────────────────────────────────────────┘ │
│  ┌─ Security Layer ───────────────────────────────────────────────────┐  │
│  │  Spring Security (Session Auth, BCrypt, RBAC, Ownership Checks)    │  │
│  └─────────────────────────────────────────────────────────────────────┘ │
│  ┌─ Intelligence Engine ──────────────────────────────────────────────┐  │
│  │  SuccessScoreService • RiskAnalysisService • RecommendationService │  │
│  │  SmartAttendanceCalculator • EduBotService • FacultyHealthService  │  │
│  └─────────────────────────────────────────────────────────────────────┘ │
│  ┌─ Service Layer (Interface + Impl) ────────────────────────────────┐   │
│  │  StudentService, AttendanceService, MarksService, FeeService, etc. │  │
│  │  GradingUtil (Credit-Weighted GPA/CGPA), DashboardService          │  │
│  └─────────────────────────────────────────────────────────────────────┘ │
│  ┌─ Persistence Layer (Spring Data JPA) ─────────────────────────────┐   │
│  │  13+ Repository Interfaces • Derived & JPQL Queries                │  │
│  │  Lazy/Eager Optimization • @Transactional Boundaries               │  │
│  └─────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────┬────────────────────────────────────────┘
                                  │ JDBC / HikariCP Connection Pool
                                  ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER (MySQL / MariaDB)                    │
│   Users • Departments • Courses • Teachers • Subjects • Students         │
│   Attendance • Marks • Fees • Assignments • Submissions                  │
│   Notices • Timetable • Support Tickets • Notifications                  │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 🚀 Modules & Features

### Core Academic Management
| Module | Description | Roles |
|--------|-------------|-------|
| 📚 **Academic Setup** | Full CRUD for Departments, Courses, Subjects, and Faculty | ADMIN |
| 👨‍🎓 **Student Profiler** | 360° student profile — attendance %, CGPA, fee status, risk level, recommendations | ALL (scoped) |
| 📋 **Attendance Marker** | Bulk class attendance (Present/Absent), percentage computation, 75% threshold alerts | TEACHER, ADMIN |
| 📝 **Marks & Grading** | 7-tier grading (A+ to F), credit-weighted semester GPA & cumulative CGPA | TEACHER, ADMIN |
| 📄 **Assignments** | Faculty posts assignments with deadlines; tracks student submissions (SUBMITTED/LATE/PENDING) | TEACHER, STUDENT |
| 💰 **Fee Ledger** | Fee statements, installment tracking, payment recording, auto-derived status | ADMIN, STUDENT |
| 📢 **Campus Notices** | Priority-based notices (URGENT/IMPORTANT/NORMAL) with target audience filtering | ALL |
| 📅 **Timetable** | Weekly schedule with rooms, subjects, and teacher assignments | ALL |
| 👤 **User Management** | Account creation, password resets, role assignment, enable/disable | ADMIN |

### Intelligence & Analytics
| Module | Description | Roles |
|--------|-------------|-------|
| 🎯 **Success Score** | Weighted composite score with component breakdown and status classification | ALL (scoped) |
| ⚠️ **Early Warning** | Automated risk detection with severity levels and intervention recommendations | TEACHER, ADMIN |
| 📊 **Analytics Dashboard** | Live counters, grade distributions, attendance overview, fee status, department charts | ADMIN, TEACHER |
| 🧮 **Smart Calculator** | Recovery classes calculator to reach attendance threshold | STUDENT |
| 💡 **Recommendations** | Personalized, data-driven academic improvement suggestions | STUDENT |
| 🏥 **Faculty Health** | Class-level health metrics with at-risk student prioritization | TEACHER |
| 🤖 **EduBot** | Conversational assistant answering data-driven questions | STUDENT |
| 🎫 **Support Tickets** | Academic doubt/support request system with faculty resolution workflow | ALL |
| 🔔 **Notifications** | System-generated alerts for attendance, deadlines, performance, and tickets | ALL |
| 🔍 **Global Search** | Unified search across students, subjects, assignments, and tickets | ALL |

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 17+ |
| **Framework** | Spring Boot 3.3.4, Spring MVC, Spring Data JPA, Spring Security |
| **Build Tool** | Apache Maven |
| **Database** | MySQL 8.0+ / MariaDB 10.11+ |
| **ORM** | Hibernate (via Spring Data JPA) |
| **Security** | Session-based auth, BCrypt encryption, Role-Based Access Control |
| **Frontend** | HTML5, CSS3 (Glassmorphism, CSS Variables), Vanilla JavaScript (ES6+), Canvas API |
| **Architecture** | Monolithic MVC — Controller → Service → Repository → Entity |

---

## 🗄️ Database Design

### Entity Relationships
```
User (1) ──── (1) Teacher          User (1) ──── (1) Student
Department (1) ──── (M) Course     Course (1) ──── (M) Subject
Teacher (1) ──── (M) Subject       Student (M) ──── (1) Course
Student (1) ──── (M) Attendance    Student (1) ──── (M) Marks
Student (1) ──── (M) Fee           Student (1) ──── (M) AssignmentSubmission
Student (1) ──── (M) SupportTicket User (1) ──── (M) Notification
```

### Key Constraints
- `Attendance`: Unique on `(student_id, subject_id, date)`
- `Marks`: Unique on `(student_id, subject_id)`
- `AssignmentSubmission`: Unique on `(student_id, assignment_id)`
- `Student`: Unique `studentCode`, `rollNumber`, `email`
- `Subject`: Unique `code`

---

## 🔑 Demo Credentials

| Role | Email | Password | Details |
|------|-------|----------|---------|
| **Admin** | `admin@student360.com` | `admin123` | Full system access |
| **Teacher** | `teacher@student360.com` | `teacher123` | Priya Sharma (CSE, CS301 & CS302) |
| **Student** | `student@student360.com` | `student123` | Rahul Verma (R1001, B.Tech CSE, Year 3) |

> On first launch, `DataSeeder` auto-populates: 3 users, 4 departments, 5 courses, 10 subjects, 5 teachers, 20 students, 140+ attendance records, marks, assignments, fees, notices, and timetable entries.

---

## ⚙️ Installation & Setup

### Prerequisites
- **JDK 17+** — [Download](https://adoptium.net/)
- **Apache Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** or **MariaDB 10.11+** — running on `localhost:3306`

### Step 1 — Configure Database
Edit `student360/backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student360?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### Step 2 — Build & Run
```bash
cd student360/backend

# Compile
mvn clean compile

# Run the application
mvn spring-boot:run
```

### Step 3 — Open in Browser
Navigate to **http://localhost:8080** and log in with the demo credentials above.

---

## 📡 API Endpoints (Summary)

| Category | Endpoints | Methods |
|----------|-----------|---------|
| **Auth** | `/api/auth/login`, `/logout`, `/me` | POST, POST, GET |
| **Students** | `/api/students`, `/api/students/{id}`, `/api/students/{id}/profile` | CRUD + GET |
| **Attendance** | `/api/attendance/mark`, `/api/attendance/student/{id}`, `/api/attendance/my` | POST, GET, GET |
| **Marks** | `/api/marks`, `/api/marks/student/{id}`, `/api/marks/my`, `/api/marks/my/gpa` | POST, GET, GET, GET |
| **Assignments** | `/api/assignments`, `/api/assignments/{id}/submissions` | CRUD + POST |
| **Fees** | `/api/fees`, `/api/fees/{id}/pay`, `/api/fees/my` | CRUD + POST + GET |
| **Notices** | `/api/notices`, `/api/notices/recent` | CRUD + GET |
| **Timetable** | `/api/timetable` | CRUD |
| **Dashboard** | `/api/dashboard/stats` | GET |
| **Success Score** | `/api/student-success/{id}`, `/api/student-success/my` | GET |
| **Faculty Health** | `/api/faculty-health/summary` | GET |
| **EduBot** | `/api/edubot/ask?q=...`, `/api/edubot/chat` | GET, POST |
| **Tickets** | `/api/tickets`, `/api/tickets/{id}/resolve`, `/api/tickets/my` | CRUD |
| **Notifications** | `/api/notifications`, `/api/notifications/read-all` | GET, PUT |
| **Search** | `/api/search?q=...` | GET |
| **Departments** | `/api/departments` | CRUD |
| **Courses** | `/api/courses` | CRUD |
| **Subjects** | `/api/subjects` | CRUD |
| **Teachers** | `/api/teachers` | CRUD |
| **Users** | `/api/users` | CRUD |

> Full API documentation: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

---

## 📂 Project Structure

```
student360/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/student360/
│       │   ├── Student360Application.java
│       │   ├── config/
│       │   │   ├── SecurityConfig.java
│       │   │   └── DataSeeder.java
│       │   ├── controller/          # REST API endpoints
│       │   ├── service/             # Business logic interfaces
│       │   │   └── impl/            # Service implementations
│       │   ├── repository/          # Spring Data JPA repositories
│       │   ├── entity/              # JPA entities & enums
│       │   ├── dto/                 # Request/Response DTOs
│       │   └── exception/           # Custom exceptions & handler
│       └── resources/
│           ├── application.properties
│           └── static/              # Frontend SPA
│               ├── index.html       # Landing page
│               ├── app.html         # Main dashboard
│               ├── css/styles.css   # Glassmorphic theme
│               └── js/              # Module scripts
├── database/                        # SQL schema reference
├── API_DOCUMENTATION.md
├── VIVA_NOTES.md
└── README.md
```

---

## 🎓 Academic Context

This project was developed as a **Full Stack Java Programming (FSJP) Mini Project** demonstrating:

- **Clean Architecture**: Strict separation — Controller → Service → Repository → Entity
- **Spring Boot Ecosystem**: Auto-configuration, dependency injection, transactional management
- **Relational Database Design**: Normalized schema with foreign keys, unique constraints, cascade rules
- **Spring Security**: Session-based auth with BCrypt, role-based endpoint protection, ownership verification
- **Business Logic in Java**: All grading, GPA computation, risk analysis, and scoring done in backend services — no external APIs
- **RESTful API Design**: Consistent JSON request/response patterns, proper HTTP status codes, global exception handling
- **Frontend Integration**: Single Page Application consuming REST APIs, with modern CSS and interactive charts

---

## 📚 Documentation
- **[API Documentation](API_DOCUMENTATION.md)** — Complete endpoint catalog with request/response schemas
- **[Viva Voce Guide](VIVA_NOTES.md)** — Technical theory and interview preparation

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

<p align="center">
  <b>Built with ❤️ using Java, Spring Boot & vanilla JavaScript</b><br>
  <i>EduSphere — Because every student's success matters.</i>
</p>
