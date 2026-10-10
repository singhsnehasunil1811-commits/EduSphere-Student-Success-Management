package com.student360.service.impl;

import com.student360.dto.DepartmentRequest;
import com.student360.dto.DepartmentResponse;
import com.student360.entity.Department;
import com.student360.exception.DuplicateResourceException;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.DepartmentRepository;
import com.student360.service.DepartmentService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repo;

    public DepartmentServiceImpl(DepartmentRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<DepartmentResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public DepartmentResponse getById(Long id) {
        return toResponse(getEntityById(id));
    }

    @Override
    public Department getEntityById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
    }

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        checkUnique(request, null);
        Department dept = new Department();
        apply(dept, request);
        return toResponse(repo.save(dept));
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department dept = getEntityById(id);
        checkUnique(request, id);
        apply(dept, request);
        return toResponse(repo.save(dept));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department dept = getEntityById(id);
        repo.delete(dept);
    }

    private void checkUnique(DepartmentRequest request, Long selfId) {
        repo.findByName(request.name().trim())
            .filter(d -> !d.getId().equals(selfId))
            .ifPresent(d -> {
                throw new DuplicateResourceException("Department with name '" + request.name() + "' already exists");
            });

        if (request.code() != null && !request.code().isBlank()) {
            repo.findByCode(request.code().trim().toUpperCase())
                .filter(d -> !d.getId().equals(selfId))
                .ifPresent(d -> {
                    throw new DuplicateResourceException("Department code '" + request.code() + "' already exists");
                });
        }
    }

    private void apply(Department dept, DepartmentRequest request) {
        dept.setName(request.name().trim());
        if (request.code() != null && !request.code().isBlank()) {
            dept.setCode(request.code().trim().toUpperCase());
        }
        dept.setHod(request.hod() != null ? request.hod().trim() : null);
        dept.setDescription(request.description() != null ? request.description().trim() : null);
    }

    private DepartmentResponse toResponse(Department d) {
        return new DepartmentResponse(
            d.getId(),
            d.getName(),
            d.getCode(),
            d.getHod(),
            d.getDescription()
        );
    }
}
