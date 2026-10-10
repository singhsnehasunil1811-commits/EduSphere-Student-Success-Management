package com.student360.service;

import com.student360.dto.FeeRequest;
import com.student360.dto.PaymentRequest;
import com.student360.dto.FeeResponse;
import java.util.List;

public interface FeeService {
    FeeResponse create(FeeRequest request);
    FeeResponse update(Long id, FeeRequest request);
    FeeResponse getById(Long id);
    List<FeeResponse> getByStudent(Long studentId);
    FeeResponse recordPayment(Long id, PaymentRequest request);
    List<FeeResponse> getMyFees(String username);
    long countPendingFees();
}
