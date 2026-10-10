package com.student360.controller;

import com.student360.dto.AttendanceRecordResponse;
import com.student360.dto.AttendanceSummaryResponse;
import com.student360.dto.BulkAttendanceRequest;
import com.student360.service.AttendanceService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AttendanceRecordResponse>> bulkMark(@Valid @RequestBody BulkAttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.bulkMark(request));
    }

    @GetMapping("/subject/{subjectId}")
    public List<AttendanceRecordResponse> getBySubjectAndDate(
        @PathVariable Long subjectId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return service.getBySubjectAndDate(subjectId, targetDate);
    }

    @GetMapping("/student/{studentId}")
    public List<AttendanceRecordResponse> getStudentRecords(
        @PathVariable Long studentId,
        @RequestParam(required = false) Long subjectId,
        Authentication auth
    ) {
        String username = auth != null ? auth.getName() : null;
        return service.getStudentRecords(studentId, subjectId, username);
    }

    @GetMapping("/student/{studentId}/summary")
    public AttendanceSummaryResponse getStudentSummary(
        @PathVariable Long studentId,
        Authentication auth
    ) {
        String username = auth != null ? auth.getName() : null;
        return service.getStudentSummary(studentId, username);
    }

    @GetMapping("/my-summary")
    public AttendanceSummaryResponse getMySummary(Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        return service.getMySummary(username);
    }
}
