package com.student360.service;

import com.student360.dto.AttendanceRecordResponse;
import com.student360.dto.AttendanceSummaryResponse;
import com.student360.dto.BulkAttendanceRequest;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    List<AttendanceRecordResponse> bulkMark(BulkAttendanceRequest request);
    List<AttendanceRecordResponse> getBySubjectAndDate(Long subjectId, LocalDate date);
    List<AttendanceRecordResponse> getStudentRecords(Long studentId, Long subjectId, String currentUsername);
    AttendanceSummaryResponse getStudentSummary(Long studentId, String currentUsername);
    AttendanceSummaryResponse getMySummary(String currentUsername);
}
