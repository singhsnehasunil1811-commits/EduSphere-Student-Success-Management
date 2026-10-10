package com.student360.controller;

import com.student360.dto.MarksRequest;
import com.student360.dto.MarksResponse;
import com.student360.dto.StudentGpaResponse;
import com.student360.service.MarksService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marks")
public class MarksController {

    private final MarksService marksService;

    public MarksController(MarksService marksService) {
        this.marksService = marksService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<MarksResponse> createOrUpdateMarks(@Valid @RequestBody MarksRequest request) {
        return ResponseEntity.ok(marksService.createOrUpdate(request));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<MarksResponse>> getStudentMarks(@PathVariable Long studentId, Authentication auth) {
        return ResponseEntity.ok(marksService.getByStudent(studentId, auth));
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<MarksResponse>> getSubjectMarks(@PathVariable Long subjectId) {
        return ResponseEntity.ok(marksService.getBySubject(subjectId));
    }

    @GetMapping("/student/{studentId}/gpa")
    public ResponseEntity<StudentGpaResponse> getStudentGpa(@PathVariable Long studentId, Authentication auth) {
        return ResponseEntity.ok(marksService.getStudentGpa(studentId, auth));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<MarksResponse>> getMyMarks(Authentication auth) {
        return ResponseEntity.ok(marksService.getMyMarks(auth));
    }

    @GetMapping("/my/gpa")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentGpaResponse> getMyGpa(Authentication auth) {
        return ResponseEntity.ok(marksService.getMyGpa(auth));
    }
}
