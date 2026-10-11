package com.student360.repository;

import com.student360.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findTop20ByOrderByTimestampDesc();

    Page<AuditLog> findByEntityNameOrderByTimestampDesc(String entityName, Pageable pageable);

    Page<AuditLog> findByPerformedByOrderByTimestampDesc(String performedBy, Pageable pageable);
}
