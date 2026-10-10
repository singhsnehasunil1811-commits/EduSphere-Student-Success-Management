package com.student360.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String courseCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "duration_years", nullable = false)
    private int durationYears; // duration in years

    @ManyToOne(optional = false)
    @JoinColumn(name = "department_id")
    private Department department;
}
