package com.student360.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String entityName;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 50)
    private String action; // CREATE, UPDATE, DELETE

    @Column(nullable = false, length = 100)
    private String performedBy;

    @Column(length = 50)
    private String userRole;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog(String entityName, Long entityId, String action, String performedBy, String userRole, String details) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.action = action;
        this.performedBy = performedBy;
        this.userRole = userRole;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
