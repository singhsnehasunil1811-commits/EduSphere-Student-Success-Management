package com.student360.repository;

import com.student360.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findBySubjectId(Long subjectId);
    List<Assignment> findByCreatedById(Long teacherId);
}
