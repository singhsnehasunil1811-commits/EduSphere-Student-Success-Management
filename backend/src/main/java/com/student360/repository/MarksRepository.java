package com.student360.repository;

import com.student360.entity.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {
    Optional<Marks> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
    List<Marks> findByStudentId(Long studentId);
    List<Marks> findBySubjectId(Long subjectId);

    @Query("SELECT m FROM Marks m WHERE m.student.id = :studentId ORDER BY m.subject.semester ASC")
    List<Marks> findByStudentIdOrderBySubjectSemesterAsc(@Param("studentId") Long studentId);
}
