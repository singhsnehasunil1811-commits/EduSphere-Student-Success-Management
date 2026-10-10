-- STUDENT360 — Intelligent Student Management System
-- Database Schema Definition (MySQL / MariaDB)

CREATE DATABASE IF NOT EXISTS `student360` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `student360`;

-- 1. Users Table (Authentication & Authorization)
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `full_name` VARCHAR(255) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  `enabled` BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Departments Table
CREATE TABLE IF NOT EXISTS `departments` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `code` VARCHAR(50) NOT NULL UNIQUE,
  `name` VARCHAR(255) NOT NULL UNIQUE,
  `hod` VARCHAR(255) DEFAULT NULL,
  `description` VARCHAR(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Courses Table
CREATE TABLE IF NOT EXISTS `courses` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `course_code` VARCHAR(50) NOT NULL UNIQUE,
  `name` VARCHAR(255) NOT NULL,
  `duration_years` INT NOT NULL,
  `department_id` BIGINT NOT NULL,
  CONSTRAINT `fk_course_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Teachers Table
CREATE TABLE IF NOT EXISTS `teachers` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `teacher_code` VARCHAR(50) NOT NULL UNIQUE,
  `first_name` VARCHAR(255) NOT NULL,
  `last_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `phone` VARCHAR(50) DEFAULT NULL,
  `designation` VARCHAR(255) DEFAULT NULL,
  `qualification` VARCHAR(255) DEFAULT NULL,
  `department_id` BIGINT DEFAULT NULL,
  `user_id` BIGINT UNIQUE DEFAULT NULL,
  CONSTRAINT `fk_teacher_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Subjects Table
CREATE TABLE IF NOT EXISTS `subjects` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `code` VARCHAR(50) NOT NULL UNIQUE,
  `name` VARCHAR(255) NOT NULL,
  `credits` INT NOT NULL,
  `semester` INT NOT NULL,
  `course_id` BIGINT NOT NULL,
  `teacher_id` BIGINT DEFAULT NULL,
  CONSTRAINT `fk_subject_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_subject_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Students Table
CREATE TABLE IF NOT EXISTS `students` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `student_code` VARCHAR(50) NOT NULL UNIQUE,
  `roll_number` VARCHAR(50) NOT NULL UNIQUE,
  `first_name` VARCHAR(255) NOT NULL,
  `last_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `phone` VARCHAR(50) DEFAULT NULL,
  `year` INT NOT NULL,
  `semester` INT NOT NULL,
  `status` VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
  `admission_date` DATE DEFAULT NULL,
  `department_id` BIGINT NOT NULL,
  `course_id` BIGINT DEFAULT NULL,
  `user_id` BIGINT UNIQUE DEFAULT NULL,
  CONSTRAINT `fk_student_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `fk_student_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Attendance Table
CREATE TABLE IF NOT EXISTS `attendance` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `student_id` BIGINT NOT NULL,
  `subject_id` BIGINT NOT NULL,
  `date` DATE NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `remarks` VARCHAR(255) DEFAULT NULL,
  CONSTRAINT `uk_attendance_record` UNIQUE (`student_id`, `subject_id`, `date`),
  CONSTRAINT `fk_attendance_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_attendance_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Marks Table
CREATE TABLE IF NOT EXISTS `marks` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `student_id` BIGINT NOT NULL,
  `subject_id` BIGINT NOT NULL,
  `internal` INT NOT NULL DEFAULT 0,
  `assignment` INT NOT NULL DEFAULT 0,
  `midterm` INT NOT NULL DEFAULT 0,
  `end_sem` INT NOT NULL DEFAULT 0,
  CONSTRAINT `uk_marks_record` UNIQUE (`student_id`, `subject_id`),
  CONSTRAINT `fk_marks_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_marks_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Assignments Table
CREATE TABLE IF NOT EXISTS `assignments` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `deadline` DATE NOT NULL,
  `max_marks` INT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `subject_id` BIGINT NOT NULL,
  `created_by_id` BIGINT NOT NULL,
  CONSTRAINT `fk_assignment_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_assignment_teacher` FOREIGN KEY (`created_by_id`) REFERENCES `teachers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Assignment Submissions Table
CREATE TABLE IF NOT EXISTS `assignment_submissions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `student_id` BIGINT NOT NULL,
  `assignment_id` BIGINT NOT NULL,
  `submitted_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `status` VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
  CONSTRAINT `uk_assignment_submission` UNIQUE (`student_id`, `assignment_id`),
  CONSTRAINT `fk_submission_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_submission_assignment` FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. Fees Table
CREATE TABLE IF NOT EXISTS `fees` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `student_id` BIGINT NOT NULL,
  `academic_year` VARCHAR(50) NOT NULL,
  `total_fees` DOUBLE NOT NULL,
  `paid_amount` DOUBLE NOT NULL DEFAULT 0.0,
  `due_date` DATE NOT NULL,
  `remarks` VARCHAR(255) DEFAULT NULL,
  CONSTRAINT `fk_fee_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. Notices Table
CREATE TABLE IF NOT EXISTS `notices` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `priority` VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
  `target_audience` VARCHAR(20) NOT NULL DEFAULT 'ALL',
  `date` DATE NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by_id` BIGINT NOT NULL,
  CONSTRAINT `fk_notice_user` FOREIGN KEY (`created_by_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. Timetable Table
CREATE TABLE IF NOT EXISTS `timetable` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `day` VARCHAR(20) NOT NULL,
  `start_time` TIME NOT NULL,
  `end_time` TIME NOT NULL,
  `room` VARCHAR(50) NOT NULL,
  `subject_id` BIGINT NOT NULL,
  `teacher_id` BIGINT NOT NULL,
  CONSTRAINT `fk_timetable_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_timetable_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
