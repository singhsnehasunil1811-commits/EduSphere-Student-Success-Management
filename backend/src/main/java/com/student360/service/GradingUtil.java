package com.student360.service;

import com.student360.dto.MarksResponse;
import com.student360.dto.SemesterGpa;

import java.util.List;

public final class GradingUtil {

    private GradingUtil() {
    }

    public static int computeTotal(int internal, int assignment, int midterm, int endSem) {
        return internal + assignment + midterm + endSem;
    }

    public static double computePercentage(int total) {
        return (total / 100.0) * 100.0;
    }

    public static String computeGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }

    public static double computeGradePoints(String grade) {
        switch (grade) {
            case "A+": return 10.0;
            case "A": return 9.0;
            case "B+": return 8.0;
            case "B": return 7.0;
            case "C": return 6.0;
            case "D": return 5.0;
            case "F": 
            default: return 0.0;
        }
    }

    public static double computeGpa(List<MarksResponse> marks) {
        double totalPoints = 0;
        int totalCredits = 0;
        for (MarksResponse m : marks) {
            totalPoints += m.gradePoints() * m.credits();
            totalCredits += m.credits();
        }
        if (totalCredits == 0) return 0.0;
        return totalPoints / totalCredits;
    }

    public static double computeCgpa(List<SemesterGpa> semesters) {
        double totalPoints = 0;
        int totalCredits = 0;
        for (SemesterGpa s : semesters) {
            totalPoints += s.gpa() * s.totalCredits();
            totalCredits += s.totalCredits();
        }
        if (totalCredits == 0) return 0.0;
        return totalPoints / totalCredits;
    }
}
