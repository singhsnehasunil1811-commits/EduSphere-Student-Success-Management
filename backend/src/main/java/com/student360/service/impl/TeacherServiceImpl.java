package com.student360.service.impl;

import com.student360.dto.TeacherRequest;
import com.student360.dto.TeacherResponse;
import com.student360.entity.Department;
import com.student360.entity.Teacher;
import com.student360.entity.User;
import com.student360.exception.DuplicateResourceException;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.DepartmentRepository;
import com.student360.repository.TeacherRepository;
import com.student360.repository.UserRepository;
import com.student360.service.TeacherService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepo;
    private final DepartmentRepository deptRepo;
    private final UserRepository userRepo;

    public TeacherServiceImpl(TeacherRepository teacherRepo, DepartmentRepository deptRepo, UserRepository userRepo) {
        this.teacherRepo = teacherRepo;
        this.deptRepo = deptRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<TeacherResponse> getAll() {
        return teacherRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<TeacherResponse> getByDepartment(Long departmentId) {
        return teacherRepo.findByDepartmentId(departmentId).stream().map(this::toResponse).toList();
    }

    @Override
    public TeacherResponse getById(Long id) {
        return toResponse(getEntityById(id));
    }

    @Override
    public Teacher getEntityById(Long id) {
        return teacherRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id " + id));
    }

    @Override
    public Teacher getEntityByUserId(Long userId) {
        return teacherRepo.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher record not linked to user id " + userId));
    }

    @Override
    public Teacher getEntityByUserEmail(String email) {
        return teacherRepo.findByUserEmail(email)
            .orElseGet(() -> teacherRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher record not found for email " + email)));
    }

    @Override
    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        checkUnique(request, null);
        Teacher teacher = new Teacher();
        apply(teacher, request);
        return toResponse(teacherRepo.save(teacher));
    }

    @Override
    @Transactional
    public TeacherResponse update(Long id, TeacherRequest request) {
        Teacher teacher = getEntityById(id);
        checkUnique(request, id);
        apply(teacher, request);
        return toResponse(teacherRepo.save(teacher));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Teacher teacher = getEntityById(id);
        teacherRepo.delete(teacher);
    }

    private void checkUnique(TeacherRequest request, Long selfId) {
        teacherRepo.findByTeacherCode(request.teacherCode().trim().toUpperCase())
            .filter(t -> !t.getId().equals(selfId))
            .ifPresent(t -> {
                throw new DuplicateResourceException("Teacher code '" + request.teacherCode() + "' already exists");
            });

        teacherRepo.findByEmail(request.email().trim().toLowerCase())
            .filter(t -> !t.getId().equals(selfId))
            .ifPresent(t -> {
                throw new DuplicateResourceException("Teacher email '" + request.email() + "' already exists");
            });
    }

    private void apply(Teacher teacher, TeacherRequest request) {
        Department dept = deptRepo.findById(request.departmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + request.departmentId()));

        teacher.setTeacherCode(request.teacherCode().trim().toUpperCase());
        teacher.setFirstName(request.firstName().trim());
        teacher.setLastName(request.lastName().trim());
        teacher.setEmail(request.email().trim().toLowerCase());
        teacher.setPhone(request.phone() != null ? request.phone().trim() : null);
        teacher.setDesignation(request.designation() != null ? request.designation().trim() : null);
        teacher.setQualification(request.qualification() != null ? request.qualification().trim() : null);
        teacher.setDepartment(dept);

        if (request.userId() != null) {
            User u = userRepo.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + request.userId()));
            teacher.setUser(u);
        }
    }

    private TeacherResponse toResponse(Teacher t) {
        return new TeacherResponse(
            t.getId(),
            t.getTeacherCode(),
            t.getFirstName(),
            t.getLastName(),
            t.getEmail(),
            t.getPhone(),
            t.getDesignation(),
            t.getQualification(),
            t.getDepartment().getId(),
            t.getDepartment().getName(),
            t.getUser() != null ? t.getUser().getId() : null
        );
    }
}
