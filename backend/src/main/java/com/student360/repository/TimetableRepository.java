package com.student360.repository;

import com.student360.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByDay(String day);
    List<Timetable> findBySubjectCourseId(Long courseId);
    List<Timetable> findByTeacherId(Long teacherId);
}
