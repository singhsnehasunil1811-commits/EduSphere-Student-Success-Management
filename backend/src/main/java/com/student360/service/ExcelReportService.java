package com.student360.service;

import com.student360.entity.Course;
import com.student360.entity.Department;
import com.student360.entity.Student;
import com.student360.repository.CourseRepository;
import com.student360.repository.DepartmentRepository;
import com.student360.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public byte[] exportStudentsToExcel() {
        List<Student> students = studentRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Students Roster");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create Header Row
            String[] headers = {"ID", "Student Code", "Roll Number", "First Name", "Last Name", "Email", "Department", "Course", "Current Year", "Status"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Populate Student Data
            int rowIdx = 1;
            for (Student s : students) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getStudentCode());
                row.createCell(2).setCellValue(s.getRollNumber());
                row.createCell(3).setCellValue(s.getFirstName());
                row.createCell(4).setCellValue(s.getLastName());
                row.createCell(5).setCellValue(s.getEmail());
                row.createCell(6).setCellValue(s.getDepartment() != null ? s.getDepartment().getName() : "N/A");
                row.createCell(7).setCellValue(s.getCourse() != null ? s.getCourse().getName() : "N/A");
                row.createCell(8).setCellValue(s.getYear());
                row.createCell(9).setCellValue(s.getStatus());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export Excel report: " + e.getMessage(), e);
        }
    }

    @Transactional
    public int importStudentsFromExcel(MultipartFile file) {
        int importedCount = 0;
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String studentCode = getCellString(row.getCell(1));
                String rollNumber = getCellString(row.getCell(2));
                String firstName = getCellString(row.getCell(3));
                String lastName = getCellString(row.getCell(4));
                String email = getCellString(row.getCell(5));

                if (studentCode.isBlank() || rollNumber.isBlank() || email.isBlank()) {
                    continue;
                }

                if (studentRepository.findByStudentCode(studentCode).isPresent() || studentRepository.findByRollNumber(rollNumber).isPresent()) {
                    continue; // Skip existing
                }

                Student student = new Student();
                student.setStudentCode(studentCode);
                student.setRollNumber(rollNumber);
                student.setFirstName(firstName);
                student.setLastName(lastName);
                student.setEmail(email);
                student.setYear(1);
                student.setSemester(1);
                student.setStatus("ACTIVE");

                // Assign default Department & Course if available
                Department dept = departmentRepository.findAll().stream().findFirst().orElse(null);
                Course course = courseRepository.findAll().stream().findFirst().orElse(null);
                student.setDepartment(dept);
                student.setCourse(course);

                studentRepository.save(student);
                importedCount++;
            }

            auditLogService.log("Student", 0L, "BATCH_IMPORT", "Batch imported " + importedCount + " students via Excel upload");
            return importedCount;
        } catch (Exception e) {
            throw new RuntimeException("Failed to import Excel data: " + e.getMessage(), e);
        }
    }

    private String getCellString(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
