package com.student360.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.student360.dto.MarksResponse;
import com.student360.dto.StudentGpaResponse;
import com.student360.entity.Student;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfTranscriptService {

    private final StudentRepository studentRepository;
    private final MarksService marksService;

    @Transactional(readOnly = true)
    public byte[] generateTranscriptPdf(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        StudentGpaResponse gpaResponse = marksService.getStudentGpa(studentId, (String) null);
        List<MarksResponse> marksList = marksService.getByStudent(studentId, (String) null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);
        PdfWriter.getInstance(document, out);

        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(30, 41, 59));
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(71, 85, 105));
        Font sectionHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(15, 23, 42));
        Font boldCellFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(51, 65, 85));
        Font cgpaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(13, 148, 136));

        // Header Table
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPaddingBottom(12);

        Paragraph title = new Paragraph("EDUSPHERE ACADEMIC SYSTEM", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(title);

        Paragraph subtitle = new Paragraph("OFFICIAL SEMESTER GRADE TRANSCRIPT & EVALUATION REPORT", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(subtitle);

        Paragraph dateLine = new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), regularFont);
        dateLine.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(dateLine);

        headerTable.addCell(headerCell);
        document.add(headerTable);

        // Divider Line
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setFixedHeight(2f);
        lineCell.setBackgroundColor(new Color(13, 148, 136));
        lineCell.setBorder(Rectangle.NO_BORDER);
        lineTable.addCell(lineCell);
        document.add(lineTable);

        document.add(new Paragraph(" ", regularFont));

        // Student Info Grid
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{20f, 30f, 20f, 30f});

        addInfoRow(infoTable, "Student Name:", student.getFirstName() + " " + student.getLastName(), "Roll Number:", student.getRollNumber(), regularFont);
        addInfoRow(infoTable, "Student Code:", student.getStudentCode(), "Email:", student.getEmail(), regularFont);
        addInfoRow(infoTable, "Course:", student.getCourse() != null ? student.getCourse().getName() : "N/A", "Department:", student.getDepartment() != null ? student.getDepartment().getName() : "N/A", regularFont);
        addInfoRow(infoTable, "Academic Year:", "Year " + student.getYear() + " (Sem " + student.getSemester() + ")", "Status:", student.getStatus(), regularFont);

        document.add(infoTable);
        document.add(new Paragraph(" ", regularFont));

        // Academic Performance Table
        Paragraph marksTitle = new Paragraph("Course Subjects & Grade Breakdown", sectionHeaderFont);
        marksTitle.setSpacingAfter(8);
        document.add(marksTitle);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{16f, 32f, 9f, 9f, 9f, 9f, 8f, 8f});

        String[] headers = {"Code", "Subject Name", "Internal", "Assign.", "Midterm", "EndSem", "Total", "Grade"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, boldCellFont));
            cell.setBackgroundColor(new Color(30, 41, 59));
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        boolean alternate = false;
        for (MarksResponse m : marksList) {
            Color rowColor = alternate ? new Color(248, 250, 252) : Color.WHITE;
            alternate = !alternate;

            addCell(table, m.subjectCode(), regularFont, Element.ALIGN_CENTER, rowColor);
            addCell(table, m.subjectName(), regularFont, Element.ALIGN_LEFT, rowColor);
            addCell(table, String.valueOf(m.internal()), regularFont, Element.ALIGN_CENTER, rowColor);
            addCell(table, String.valueOf(m.assignment()), regularFont, Element.ALIGN_CENTER, rowColor);
            addCell(table, String.valueOf(m.midterm()), regularFont, Element.ALIGN_CENTER, rowColor);
            addCell(table, String.valueOf(m.endSem()), regularFont, Element.ALIGN_CENTER, rowColor);
            addCell(table, String.valueOf(m.total()), regularFont, Element.ALIGN_CENTER, rowColor);
            addCell(table, m.grade(), regularFont, Element.ALIGN_CENTER, rowColor);
        }

        document.add(table);
        document.add(new Paragraph(" ", regularFont));

        // CGPA Summary Box
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new float[]{50f, 50f});

        PdfPCell c1 = new PdfPCell(new Phrase("Cumulative Grade Point Average (CGPA): " + String.format("%.2f / 10.00", gpaResponse.cgpa()), cgpaFont));
        c1.setBackgroundColor(new Color(240, 253, 250));
        c1.setBorderColor(new Color(13, 148, 136));
        c1.setPadding(10);
        summaryTable.addCell(c1);

        String academicStanding = gpaResponse.cgpa() >= 9.0 ? "First Class with Distinction" :
                gpaResponse.cgpa() >= 7.5 ? "First Class" :
                        gpaResponse.cgpa() >= 6.0 ? "Second Class" : "Pass";

        PdfPCell c2 = new PdfPCell(new Phrase("Academic Standing: " + academicStanding, sectionHeaderFont));
        c2.setBackgroundColor(new Color(240, 253, 250));
        c2.setBorderColor(new Color(13, 148, 136));
        c2.setPadding(10);
        summaryTable.addCell(c2);

        document.add(summaryTable);

        // Verification Footer
        document.add(new Paragraph(" ", regularFont));
        Paragraph footer = new Paragraph("This document is an electronically generated academic record by EduSphere Intelligent System. Signed and verified for official institutional purposes.", regularFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    private void addInfoRow(PdfPTable table, String label1, String val1, String label2, String val2, Font font) {
        Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(30, 41, 59));
        PdfPCell c1 = new PdfPCell(new Phrase(label1, bold));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(4);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(val1, font));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setPadding(4);
        table.addCell(c2);

        PdfPCell c3 = new PdfPCell(new Phrase(label2, bold));
        c3.setBorder(Rectangle.NO_BORDER);
        c3.setPadding(4);
        table.addCell(c3);

        PdfPCell c4 = new PdfPCell(new Phrase(val2, font));
        c4.setBorder(Rectangle.NO_BORDER);
        c4.setPadding(4);
        table.addCell(c4);
    }

    private void addCell(PdfPTable table, String text, Font font, int align, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setPadding(5);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(new Color(226, 232, 240));
        table.addCell(cell);
    }
}
