package com.student360.controller;

import com.student360.dto.GradeSimulationResponse;
import com.student360.entity.AuditLog;
import com.student360.entity.Student;
import com.student360.repository.StudentRepository;
import com.student360.service.AuditLogService;
import com.student360.service.ExcelReportService;
import com.student360.service.GradeSimulationService;
import com.student360.service.PdfTranscriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advanced")
@RequiredArgsConstructor
public class AdvancedFeaturesController {

    private final GradeSimulationService gradeSimulationService;
    private final PdfTranscriptService pdfTranscriptService;
    private final ExcelReportService excelReportService;
    private final AuditLogService auditLogService;
    private final StudentRepository studentRepository;

    /**
     * 1. Dynamic Grade Goal Simulator: Simulates needed marks for target CGPA.
     */
    @GetMapping("/simulate-grade")
    public ResponseEntity<GradeSimulationResponse> simulateGoal(
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "8.5") double targetCgpa) {
        return ResponseEntity.ok(gradeSimulationService.simulateGoal(studentId, targetCgpa));
    }

    @GetMapping("/simulate-grade/my")
    public ResponseEntity<GradeSimulationResponse> simulateMyGoal(
            @RequestParam(defaultValue = "8.5") double targetCgpa,
            Authentication auth) {
        Student student = studentRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Student record not linked to user: " + auth.getName()));
        return ResponseEntity.ok(gradeSimulationService.simulateGoal(student.getId(), targetCgpa));
    }

    /**
     * 2. Official PDF Transcript Download.
     */
    @GetMapping("/transcript/pdf/{studentId}")
    public ResponseEntity<byte[]> downloadTranscriptPdf(@PathVariable Long studentId) {
        byte[] pdfBytes = pdfTranscriptService.generateTranscriptPdf(studentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transcript_student_" + studentId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/transcript/pdf/my")
    public ResponseEntity<byte[]> downloadMyTranscriptPdf(Authentication auth) {
        Student student = studentRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Student record not linked to user: " + auth.getName()));
        byte[] pdfBytes = pdfTranscriptService.generateTranscriptPdf(student.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"official_transcript_" + student.getRollNumber() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /**
     * 3. Excel Roster Export.
     */
    @GetMapping("/export/students-excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> exportStudentsExcel() {
        byte[] excelBytes = excelReportService.exportStudentsToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students_roster.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    /**
     * 4. Batch Student Import from Excel.
     */
    @PostMapping("/import/students-excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importStudentsExcel(@RequestParam("file") MultipartFile file) {
        int count = excelReportService.importStudentsFromExcel(file);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "importedCount", count,
                "message", "Successfully imported " + count + " students into EduSphere."
        ));
    }

    /**
     * 5. Audit Log System: view system security audit records.
     */
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditLogService.getRecentLogs());
    }
}
