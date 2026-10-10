package com.student360.service.impl;

import com.student360.dto.FeeRequest;
import com.student360.dto.PaymentRequest;
import com.student360.dto.FeeResponse;
import com.student360.entity.Fee;
import com.student360.entity.Student;
import com.student360.entity.User;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.FeeRepository;
import com.student360.repository.StudentRepository;
import com.student360.repository.UserRepository;
import com.student360.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeeServiceImpl implements FeeService {
    private final FeeRepository feeRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Override
    public FeeResponse create(FeeRequest request) {
        Student student = studentRepository.findById(request.studentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            
        Fee fee = new Fee();
        fee.setStudent(student);
        fee.setAcademicYear(request.academicYear());
        fee.setTotalFees(request.totalFees());
        fee.setPaidAmount(0.0);
        fee.setDueDate(request.dueDate());
        fee.setRemarks(request.remarks());
        
        return mapToResponse(feeRepository.save(fee));
    }

    @Override
    public FeeResponse update(Long id, FeeRequest request) {
        Fee fee = feeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee not found"));
            
        Student student = studentRepository.findById(request.studentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            
        fee.setStudent(student);
        fee.setAcademicYear(request.academicYear());
        fee.setTotalFees(request.totalFees());
        fee.setDueDate(request.dueDate());
        fee.setRemarks(request.remarks());
        
        return mapToResponse(feeRepository.save(fee));
    }

    @Override
    public FeeResponse getById(Long id) {
        return feeRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Fee not found"));
    }

    @Override
    public List<FeeResponse> getByStudent(Long studentId) {
        return feeRepository.findByStudentId(studentId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public FeeResponse recordPayment(Long id, PaymentRequest request) {
        Fee fee = feeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee not found"));
            
        fee.setPaidAmount(fee.getPaidAmount() + request.amount());
        if(request.remarks() != null) {
            fee.setRemarks(fee.getRemarks() + " | Payment: " + request.remarks());
        }
        
        return mapToResponse(feeRepository.save(fee));
    }

    @Override
    public List<FeeResponse> getMyFees(String username) {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Student student = studentRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            
        return feeRepository.findByStudentId(student.getId()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public long countPendingFees() {
        return feeRepository.countByPaidAmountLessThanTotalFees();
    }
    
    private FeeResponse mapToResponse(Fee fee) {
        double remaining = fee.getTotalFees() - fee.getPaidAmount();
        String status;
        if (remaining <= 0) {
            status = "PAID";
        } else if (fee.getPaidAmount() > 0) {
            status = "PARTIALLY_PAID";
        } else {
            status = "PENDING";
        }
        
        return new FeeResponse(
            fee.getId(),
            fee.getStudent().getId(),
            fee.getStudent().getUser().getFirstName() + " " + fee.getStudent().getUser().getLastName(),
            fee.getStudent().getRollNumber(),
            fee.getAcademicYear(),
            fee.getTotalFees(),
            fee.getPaidAmount(),
            remaining,
            fee.getDueDate(),
            fee.getRemarks(),
            status
        );
    }
}
