package com.student360.repository;

import com.student360.entity.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentIdAndSubjectIdAndDate(Long studentId, Long subjectId, LocalDate date);

    List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);

    List<Attendance> findByStudentIdAndSubjectIdOrderByDateDesc(Long studentId, Long subjectId);

    List<Attendance> findBySubjectIdAndDate(Long subjectId, LocalDate date);

    List<Attendance> findBySubjectId(Long subjectId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId")
    long countTotalByStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = 'PRESENT'")
    long countPresentByStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a")
    long countAllAttendance();

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.status = 'PRESENT'")
    long countAllPresent();
}
