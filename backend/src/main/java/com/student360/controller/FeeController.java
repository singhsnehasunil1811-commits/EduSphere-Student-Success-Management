package com.student360.controller;

import com.student360.dto.FeeRequest;
import com.student360.dto.PaymentRequest;
import com.student360.dto.FeeResponse;
import com.student360.service.FeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeeController {
    private final FeeService feeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeeResponse create(@Valid @RequestBody FeeRequest request) {
        return feeService.create(request);
    }

    @PutMapping("/{id}")
    public FeeResponse update(@PathVariable Long id, @Valid @RequestBody FeeRequest request) {
        return feeService.update(id, request);
    }

    @GetMapping("/{id}")
    public FeeResponse getById(@PathVariable Long id) {
        return feeService.getById(id);
    }

    @PostMapping("/{id}/pay")
    public FeeResponse recordPayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        return feeService.recordPayment(id, request);
    }

    @GetMapping("/student/{studentId}")
    public List<FeeResponse> getByStudent(@PathVariable Long studentId) {
        return feeService.getByStudent(studentId);
    }

    @GetMapping("/my")
    public List<FeeResponse> getMyFees(Authentication authentication) {
        return feeService.getMyFees(authentication.getName());
    }
}
