package com.student360.service.impl;

import com.student360.dto.CourseRequest;
import com.student360.dto.CourseResponse;
import com.student360.entity.Course;
import com.student360.entity.Department;
import com.student360.exception.DuplicateResourceException;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.CourseRepository;
import com.student360.repository.DepartmentRepository;
import com.student360.service.CourseService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepo;
    private final DepartmentRepository deptRepo;

    public CourseServiceImpl(CourseRepository courseRepo, DepartmentRepository deptRepo) {
        this.courseRepo = courseRepo;
        this.deptRepo = deptRepo;
    }

    @Override
    public List<CourseResponse> getAll() {
        return courseRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<CourseResponse> getByDepartment(Long departmentId) {
        return courseRepo.findByDepartmentId(departmentId).stream().map(this::toResponse).toList();
    }

    @Override
    public CourseResponse getById(Long id) {
        return toResponse(getEntityById(id));
    }

    @Override
    public Course getEntityById(Long id) {
        return courseRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + id));
    }

    @Override
    @Transactional
    public CourseResponse create(CourseRequest request) {
        checkUnique(request, null);
        Department dept = deptRepo.findById(request.departmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + request.departmentId()));
        Course course = new Course();
        course.setCourseCode(request.courseCode().trim().toUpperCase());
        course.setName(request.name().trim());
        course.setDurationYears(request.durationYears());
        course.setDepartment(dept);
        return toResponse(courseRepo.save(course));
    }

    @Override
    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = getEntityById(id);
        checkUnique(request, id);
        Department dept = deptRepo.findById(request.departmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + request.departmentId()));
        course.setCourseCode(request.courseCode().trim().toUpperCase());
        course.setName(request.name().trim());
        course.setDurationYears(request.durationYears());
        course.setDepartment(dept);
        return toResponse(courseRepo.save(course));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Course course = getEntityById(id);
        courseRepo.delete(course);
    }

    private void checkUnique(CourseRequest request, Long selfId) {
        courseRepo.findByCourseCode(request.courseCode().trim().toUpperCase())
            .filter(c -> !c.getId().equals(selfId))
            .ifPresent(c -> {
                throw new DuplicateResourceException("Course code '" + request.courseCode() + "' already exists");
            });
    }

    private CourseResponse toResponse(Course c) {
        return new CourseResponse(
            c.getId(),
            c.getCourseCode(),
            c.getName(),
            c.getDurationYears(),
            c.getDepartment().getId(),
            c.getDepartment().getName()
        );
    }
}
