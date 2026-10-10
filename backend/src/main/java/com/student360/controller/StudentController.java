package com.student360.controller;

import com.student360.dto.PageResponse;
import com.student360.dto.StudentProfileResponse;
import com.student360.dto.StudentRequest;
import com.student360.dto.StudentResponse;
import com.student360.service.StudentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<StudentResponse> list(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Long departmentId,
        @RequestParam(required = false) Long courseId,
        @RequestParam(required = false) Integer semester
    ) {
        if (courseId != null && semester != null) {
            return service.getByCourseAndSemester(courseId, semester);
        }
        if (courseId != null) {
            return service.getByCourse(courseId);
        }
        if (departmentId != null) {
            return service.getByDepartment(departmentId);
        }
        return service.search(q);
    }

    @GetMapping("/paged")
    public PageResponse<StudentResponse> listPaged(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Long departmentId,
        @RequestParam(required = false) Long courseId,
        @RequestParam(required = false) Integer semester,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return service.getFilteredStudents(
            q, departmentId, courseId, semester, year, status, page, size, sortBy, sortDir
        );
    }

    @GetMapping("/{id}")
    public StudentResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/{id}/profile")
    public StudentProfileResponse profile(@PathVariable Long id, Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        return service.getStudentProfile(id, username);
    }

    @GetMapping("/profile/me")
    public StudentProfileResponse myProfile(Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        return service.getMyProfile(username);
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
