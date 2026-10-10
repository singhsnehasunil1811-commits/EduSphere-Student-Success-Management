# STUDENT360 — Viva Voce Preparation & Technical Architecture Guide

Comprehensive answers to frequent viva questions regarding Java Full Stack, Spring Boot, MySQL, and Software Architecture.

---

### 1. Why Spring Boot instead of classic Spring MVC?
- **Auto-Configuration**: Spring Boot analyzes JAR dependencies on the classpath and configures sensible defaults automatically without requiring complex XML or boilerplate Java configurations.
- **Embedded Web Server**: It includes an embedded Tomcat container, meaning applications run as a standalone JAR via `java -jar` without requiring a separate web server deployment.
- **Starter POMs**: Curated dependency descriptors (like `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`) simplify dependency version management.
- **Production-Ready Features**: Built-in health metrics, actuator endpoints, and externalized properties (`application.properties`).

---

### 2. What is MVC (Model - View - Controller)?
- **Model**: Represents the application state and business data (`Entity`, `DTO`).
- **View**: The UI presentation layer rendered to the client (our static HTML/CSS/JS frontend).
- **Controller**: Intercepts HTTP client requests, invokes business services, and returns appropriate responses or views (`@RestController`).

---

### 3. What makes an API RESTful?
REST (Representational State Transfer) is an architectural style based on:
1. **Stateless communication**: Each request contains all information needed to process it.
2. **Standard HTTP methods**:
   - `GET` to retrieve resources
   - `POST` to create resources
   - `PUT` to update/replace resources
   - `DELETE` to remove resources
3. **Standard HTTP status codes**: `200 OK`, `201 Created`, `204 No Content`, `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, `409 Conflict`.
4. **JSON payloads**: Lightweight and language-agnostic data exchange format.

---

### 4. What is the difference between JPA and Hibernate?
- **JPA (Jakarta Persistence API)** is a Java standard specification defining interfaces, annotations (`@Entity`, `@Table`, `@Id`, `@ManyToOne`), and rules for Object-Relational Mapping (ORM).
- **Hibernate** is the concrete implementation (ORM provider) of the JPA specification. Spring Boot uses Hibernate under the hood by default.

---

### 5. What are Inversion of Control (IoC) and Dependency Injection (DI)?
- **IoC (Inversion of Control)**: The control of object lifecycle and creation is shifted from manual instantiation (`new Service()`) to the Spring ApplicationContext container.
- **DI (Dependency Injection)**: The mechanism where the IoC container injects required dependencies into components at runtime (via constructor injection).
  - *Advantage*: High modularity, loose coupling, and straightforward unit testing using mocks.

---

### 6. Why use the DTO (Data Transfer Object) Pattern?
- **Security**: Prevents exposing internal domain model details, database table schemas, or sensitive properties (like password hashes).
- **Performance**: Prevents accidental loading of large lazy-loaded collections or infinite circular JSON serialization loops (`Student` -> `User` -> `Student`).
- **Validation**: Jakarta Bean Validation (`@NotBlank`, `@Min`, `@Max`) is applied directly to request DTOs before touching entities.

---

### 7. What is the Layered Monolith Architecture in Student360?
```
Browser / Frontend Client
         │ HTTP Request (JSON)
         ▼
 Controller Layer (@RestController) ── Validates request DTOs
         │ Passes DTO
         ▼
 Service Layer (Interface + Impl)   ── Enforces business rules, security ownership, calculations
         │ Calls Entity/Repo
         ▼
 Repository Layer (Spring Data JPA) ── Translates to SQL queries
         │ JDBC
         ▼
 MySQL Database                     ── Persistent relational storage
```

---

### 8. How does Authentication and Session Security work?
1. The user sends credentials (`email`, `password`) to `/api/auth/login`.
2. Spring Security's `AuthenticationManager` invokes our `UserDetailsService`.
3. `BCryptPasswordEncoder` verifies the plaintext password against the hashed string stored in the database.
4. Upon successful authentication, a standard HTTP session (`JSESSIONID` cookie) is created and tied to the client.
5. In subsequent requests, the `SecurityFilterChain` verifies role authorizations (`hasRole('ADMIN')`, `hasAnyRole(...)`) and custom JSON entry points return `401` or `403` if unauthorized.

---

### 9. Why is BCrypt used for password storage?
BCrypt is a salted, slow adaptive cryptographic hash function based on the Blowfish cipher. It includes:
- **Random Salt**: Protects against pre-computed Rainbow Table attacks.
- **Configurable Work Factor (Cost factor)**: Can be increased over time as hardware becomes faster to resist brute-force cracking.

---

### 10. How are Database Relationships modeled in Student360?
- **`@OneToOne`**: `Teacher` <-> `User`, `Student` <-> `User`. Each teacher/student profile maps to exactly one login account.
- **`@ManyToOne`**: `Student` -> `Department`, `Course` -> `Department`, `Subject` -> `Course`. Many students belong to one department.
- **Composite Unique Constraints**: In `Attendance`, `@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "subject_id", "date"}))` guarantees that a student cannot have duplicate attendance records for the same lecture.

---

### 11. How does Global Exception Handling work?
- Annotated with `@RestControllerAdvice`.
- Methods with `@ExceptionHandler` intercept specific application exceptions:
  - `ResourceNotFoundException` -> `404 Not Found`
  - `DuplicateResourceException` -> `409 Conflict`
  - `MethodArgumentNotValidException` -> `400 Bad Request` with field error map
  - `AccessDeniedException` -> `403 Forbidden`
- Ensures a unified JSON format across all APIs:
  ```json
  {
    "status": 400,
    "message": "Validation failed",
    "errors": { "email": "Invalid email format" }
  }
  ```

---

### 12. Complete Request Flow Step-by-Step
1. User clicks "Save Attendance" in the browser.
2. `Attendance.saveBulkAttendance()` in `attendance.js` sends an HTTP `POST` request with JSON to `/api/attendance/bulk`.
3. Tomcat routes the request through Spring's `SecurityFilterChain` (verifies session & `ADMIN`/`TEACHER` role).
4. `AttendanceController.markBulk()` receives the request and validates the `@Valid BulkAttendanceRequest` record.
5. `AttendanceServiceImpl.markBulk()` executes:
   - Validates existence of subject and students.
   - Enforces unique constraint per student-date.
   - Saves records via `AttendanceRepository`.
6. Hibernate translates the entity instances into `INSERT / UPDATE` SQL statements over JDBC to MySQL.
7. MySQL commits the transaction.
8. The service maps entities to DTO responses.
9. Spring serializes the response DTO to JSON and sends `200 OK` back to the browser.
10. `API.toast()` displays "Attendance successfully saved" and refreshes the UI dynamically.
