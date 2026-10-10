package com.student360.controller;

import com.student360.dto.SubjectRequest;
import com.student360.dto.SubjectResponse;
import com.student360.service.SubjectService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<SubjectResponse> all(
        @RequestParam(required = false) Long courseId,
        @RequestParam(required = false) Long teacherId,
        @RequestParam(required = false) Integer semester
    ) {
        if (courseId != null && semester != null) {
            return service.getByCourseAndSemester(courseId, semester);
        }
        if (courseId != null) {
            return service.getByCourse(courseId);
        }
        if (teacherId != null) {
            return service.getByTeacher(teacherId);
        }
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SubjectResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public SubjectResponse update(@PathVariable Long id, @Valid @RequestBody SubjectRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
