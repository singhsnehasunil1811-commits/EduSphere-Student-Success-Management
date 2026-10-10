package com.student360.repository;

import com.student360.entity.Student;
import com.student360.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumber(String rollNumber);
    Optional<Student> findByStudentCode(String studentCode);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByUser(User user);
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByUserEmail(String email);
    List<Student> findByDepartmentId(Long departmentId);
    List<Student> findByCourseId(Long courseId);
    List<Student> findByCourseIdAndSemester(Long courseId, int semester);
    long countByStatus(String status);

    @Query("select s from Student s where lower(s.firstName) like lower(concat('%',:q,'%')) " +
           "or lower(s.lastName) like lower(concat('%',:q,'%')) " +
           "or lower(s.rollNumber) like lower(concat('%',:q,'%')) " +
           "or lower(s.studentCode) like lower(concat('%',:q,'%')) order by s.id")
    List<Student> search(@Param("q") String q);

    @Query("select s.department.name, count(s) from Student s group by s.department.name")
    List<Object[]> countByDepartment();
}
