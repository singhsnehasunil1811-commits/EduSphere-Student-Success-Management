package com.student360.service.impl;

import com.student360.dto.SubjectRequest;
import com.student360.dto.SubjectResponse;
import com.student360.entity.Course;
import com.student360.entity.Subject;
import com.student360.entity.Teacher;
import com.student360.exception.DuplicateResourceException;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.CourseRepository;
import com.student360.repository.SubjectRepository;
import com.student360.repository.TeacherRepository;
import com.student360.service.SubjectService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepo;
    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;

    public SubjectServiceImpl(SubjectRepository subjectRepo, CourseRepository courseRepo, TeacherRepository teacherRepo) {
        this.subjectRepo = subjectRepo;
        this.courseRepo = courseRepo;
        this.teacherRepo = teacherRepo;
    }

    @Override
    public List<SubjectResponse> getAll() {
        return subjectRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<SubjectResponse> getByCourse(Long courseId) {
        return subjectRepo.findByCourseId(courseId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<SubjectResponse> getByTeacher(Long teacherId) {
        return subjectRepo.findByTeacherId(teacherId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<SubjectResponse> getByCourseAndSemester(Long courseId, int semester) {
        return subjectRepo.findByCourseIdAndSemester(courseId, semester).stream().map(this::toResponse).toList();
    }

    @Override
    public SubjectResponse getById(Long id) {
        return toResponse(getEntityById(id));
    }

    @Override
    public Subject getEntityById(Long id) {
        return subjectRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id " + id));
    }

    @Override
    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        checkUnique(request, null);
        Subject subject = new Subject();
        apply(subject, request);
        return toResponse(subjectRepo.save(subject));
    }

    @Override
    @Transactional
    public SubjectResponse update(Long id, SubjectRequest request) {
        Subject subject = getEntityById(id);
        checkUnique(request, id);
        apply(subject, request);
        return toResponse(subjectRepo.save(subject));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Subject subject = getEntityById(id);
        subjectRepo.delete(subject);
    }

    private void checkUnique(SubjectRequest request, Long selfId) {
        subjectRepo.findByCode(request.code().trim().toUpperCase())
            .filter(s -> !s.getId().equals(selfId))
            .ifPresent(s -> {
                throw new DuplicateResourceException("Subject code '" + request.code() + "' already exists");
            });
    }

    private void apply(Subject subject, SubjectRequest request) {
        Course course = courseRepo.findById(request.courseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + request.courseId()));

        Teacher teacher = null;
        if (request.teacherId() != null) {
            teacher = teacherRepo.findById(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id " + request.teacherId()));
        }

        subject.setCode(request.code().trim().toUpperCase());
        subject.setName(request.name().trim());
        subject.setCredits(request.credits());
        subject.setSemester(request.semester());
        subject.setCourse(course);
        subject.setTeacher(teacher);
    }

    private SubjectResponse toResponse(Subject s) {
        return new SubjectResponse(
            s.getId(),
            s.getCode(),
            s.getName(),
            s.getCredits(),
            s.getSemester(),
            s.getCourse().getId(),
            s.getCourse().getName(),
            s.getTeacher() != null ? s.getTeacher().getId() : null,
            s.getTeacher() != null ? (s.getTeacher().getFirstName() + " " + s.getTeacher().getLastName()) : null
        );
    }
}
