package com.student360.repository;

import com.student360.entity.Teacher;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByTeacherCode(String teacherCode);
    Optional<Teacher> findByEmail(String email);
    Optional<Teacher> findByUserId(Long userId);
    Optional<Teacher> findByUserEmail(String email);
    List<Teacher> findByDepartmentId(Long departmentId);
}
