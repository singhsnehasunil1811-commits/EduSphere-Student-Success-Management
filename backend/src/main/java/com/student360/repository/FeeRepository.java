package com.student360.repository;

import com.student360.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findByStudentId(Long studentId);
    Optional<Fee> findByStudentIdAndAcademicYear(Long studentId, String academicYear);
    
    @Query("SELECT COUNT(f) FROM Fee f WHERE f.paidAmount < f.totalFees")
    long countByPaidAmountLessThanTotalFees();
}
