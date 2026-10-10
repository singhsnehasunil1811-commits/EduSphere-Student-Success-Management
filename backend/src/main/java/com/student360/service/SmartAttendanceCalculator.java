package com.student360.service;

import org.springframework.stereotype.Service;

@Service
public class SmartAttendanceCalculator {

    public int calculateConsecutiveClassesNeeded(int attended, int total, double targetThreshold) {
        if (total == 0) return 0;
        double currentPercentage = (double) attended / total;
        if (currentPercentage >= targetThreshold) {
            return 0;
        }
        
        // (attended + x) / (total + x) = targetThreshold
        // attended + x = targetThreshold * total + targetThreshold * x
        // x - targetThreshold * x = targetThreshold * total - attended
        // x * (1 - targetThreshold) = targetThreshold * total - attended
        // x = (targetThreshold * total - attended) / (1 - targetThreshold)
        
        double required = (targetThreshold * total - attended) / (1.0 - targetThreshold);
        return (int) Math.ceil(required);
    }

    public String generateRecoveryMessage(int attended, int total, double targetThreshold) {
        int needed = calculateConsecutiveClassesNeeded(attended, total, targetThreshold);
        if (needed <= 0) {
            return "Attendance is on track.";
        } else {
            return "Attend the next " + needed + " consecutive classes to reach " + (int)(targetThreshold * 100) + "%.";
        }
    }
}
