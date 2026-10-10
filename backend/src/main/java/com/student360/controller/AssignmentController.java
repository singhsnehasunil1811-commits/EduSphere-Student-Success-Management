package com.student360.controller;

import com.student360.dto.AssignmentRequest;
import com.student360.dto.AssignmentResponse;
import com.student360.dto.SubmissionRequest;
import com.student360.dto.SubmissionResponse;
import com.student360.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @GetMapping
    public List<AssignmentResponse> getAll() {
        return assignmentService.getAll();
    }

    @GetMapping("/{id}")
    public AssignmentResponse getById(@PathVariable Long id) {
        return assignmentService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponse create(@Valid @RequestBody AssignmentRequest request) {
        return assignmentService.create(request);
    }

    @PutMapping("/{id}")
    public AssignmentResponse update(@PathVariable Long id, @Valid @RequestBody AssignmentRequest request) {
        return assignmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        assignmentService.delete(id);
    }

    @PostMapping("/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponse submit(@Valid @RequestBody SubmissionRequest request) {
        return assignmentService.submit(request);
    }

    @GetMapping("/{id}/submissions")
    public List<SubmissionResponse> getSubmissions(@PathVariable Long id) {
        return assignmentService.getSubmissions(id);
    }

    @GetMapping("/student/{studentId}")
    public List<SubmissionResponse> getStudentSubmissions(@PathVariable Long studentId) {
        return assignmentService.getStudentSubmissions(studentId);
    }
}
