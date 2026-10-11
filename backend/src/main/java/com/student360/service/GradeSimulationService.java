package com.student360.service;

import com.student360.dto.GradeSimulationResponse;
import com.student360.entity.Marks;
import com.student360.entity.Student;
import com.student360.entity.Subject;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.MarksRepository;
import com.student360.repository.StudentRepository;
import com.student360.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeSimulationService {

    private final StudentRepository studentRepository;
    private final MarksRepository marksRepository;
    private final SubjectRepository subjectRepository;
    private final MarksService marksService;

    @Transactional(readOnly = true)
    public GradeSimulationResponse simulateGoal(Long studentId, double targetCgpa) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        var gpaResponse = marksService.getStudentGpa(studentId, (String) null);
        double currentCgpa = gpaResponse.cgpa();
        int completedSemesters = gpaResponse.semesters() != null ? gpaResponse.semesters().size() : 0;
        int nextSemester = completedSemesters + 1;

        // Formula for required next-semester GPA:
        // Target CGPA = (currentCGPA * completedSemesters + requiredGPA) / (completedSemesters + 1)
        // requiredGPA = Target CGPA * (completedSemesters + 1) - currentCGPA * completedSemesters
        double requiredGpa;
        if (completedSemesters == 0) {
            requiredGpa = targetCgpa;
        } else {
            requiredGpa = (targetCgpa * (completedSemesters + 1)) - (currentCgpa * completedSemesters);
        }

        boolean achievable = requiredGpa <= 10.0 && requiredGpa >= 0.0;
        String feasibility;
        String advice;

        if (requiredGpa > 10.0) {
            feasibility = "MATHEMATICALLY_UNREACHABLE";
            advice = String.format("A target CGPA of %.2f requires an impossible GPA of %.2f in Semester %d. Highest achievable next CGPA is %.2f.",
                    targetCgpa, requiredGpa, nextSemester, ((currentCgpa * completedSemesters + 10.0) / (completedSemesters + 1)));
        } else if (requiredGpa >= 9.0) {
            feasibility = "CHALLENGING";
            advice = String.format("Target is achievable with high distinction. You need a GPA of %.2f in Semester %d (majority A+ / 90%%+ marks).",
                    requiredGpa, nextSemester);
        } else if (requiredGpa >= 7.5) {
            feasibility = "REALISTIC";
            advice = String.format("Target is fully realistic. Maintain an average GPA of %.2f with consistent assignments and 70%%+ test marks.",
                    requiredGpa, nextSemester);
        } else {
            feasibility = "ACHIEVED_OR_COMFORTABLE";
            advice = String.format("Target requires GPA of %.2f, which is well within your current performance trajectory.", requiredGpa);
        }

        // Get student's current subjects and calculate needed end-sem exam marks
        List<Marks> currentMarksList = marksRepository.findByStudentId(studentId);
        Map<Long, Marks> marksMap = currentMarksList.stream()
                .collect(Collectors.toMap(m -> m.getSubject().getId(), m -> m, (a, b) -> a));

        List<Subject> subjects = student.getCourse() != null
                ? subjectRepository.findByCourseId(student.getCourse().getId())
                : List.of();

        List<GradeSimulationResponse.SubjectTarget> breakdown = new ArrayList<>();
        for (Subject sub : subjects) {
            Marks m = marksMap.get(sub.getId());
            int internalScore = 0;
            if (m != null) {
                internalScore = m.getInternal() + m.getAssignment() + m.getMidterm(); // max 70
            }

            // Target percentage based on required GPA: e.g. requiredGpa 8.0 -> 80 marks total
            int targetTotal = (int) Math.min(100, Math.max(40, Math.round(requiredGpa * 10)));
            int neededEndSem = Math.max(0, targetTotal - internalScore);
            neededEndSem = Math.min(30, neededEndSem); // End sem is max 30

            String recGrade = GradingUtil.computeGrade(targetTotal);

            breakdown.add(new GradeSimulationResponse.SubjectTarget(
                    sub.getId(),
                    sub.getCode(),
                    sub.getName(),
                    sub.getCredits(),
                    internalScore,
                    neededEndSem,
                    recGrade
            ));
        }

        return new GradeSimulationResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                Math.round(currentCgpa * 100.0) / 100.0,
                targetCgpa,
                Math.round(requiredGpa * 100.0) / 100.0,
                achievable,
                feasibility,
                advice,
                breakdown
        );
    }
}
