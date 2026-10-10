package com.student360.repository;

import com.student360.entity.Subject;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);
    List<Subject> findByCourseId(Long courseId);
    List<Subject> findByTeacherId(Long teacherId);
    List<Subject> findByCourseIdAndSemester(Long courseId, int semester);
}
