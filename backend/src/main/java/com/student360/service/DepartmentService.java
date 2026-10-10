package com.student360.service;

import com.student360.dto.DepartmentRequest;
import com.student360.dto.DepartmentResponse;
import com.student360.entity.Department;
import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAll();
    DepartmentResponse getById(Long id);
    Department getEntityById(Long id);
    DepartmentResponse create(DepartmentRequest request);
    DepartmentResponse update(Long id, DepartmentRequest request);
    void delete(Long id);
}
