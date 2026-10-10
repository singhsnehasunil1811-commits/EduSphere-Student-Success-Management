-- STUDENT360 — Sample Realistic Demo Seed Data (Reference SQL)
-- Default passwords are BCrypt hashes for 'admin123', 'teacher123', 'student123'
USE `student360`;

-- 1. Users
INSERT INTO `users` (`id`, `email`, `password`, `full_name`, `role`, `enabled`) VALUES
(1, 'admin@student360.com', '$2a$10$w09uY4B0p9E6iXgU.Wv49eN5FpE2jKzXlS7F00cYb9oA5o/g5G6C2', 'Admin User', 'ADMIN', 1),
(2, 'teacher@student360.com', '$2a$10$w09uY4B0p9E6iXgU.Wv49eN5FpE2jKzXlS7F00cYb9oA5o/g5G6C2', 'Priya Sharma', 'TEACHER', 1),
(3, 'rajesh.gupta@student360.com', '$2a$10$w09uY4B0p9E6iXgU.Wv49eN5FpE2jKzXlS7F00cYb9oA5o/g5G6C2', 'Dr. Rajesh Gupta', 'TEACHER', 1),
(4, 'sunita.d@student360.com', '$2a$10$w09uY4B0p9E6iXgU.Wv49eN5FpE2jKzXlS7F00cYb9oA5o/g5G6C2', 'Sunita Deshmukh', 'TEACHER', 1),
(5, 'student@student360.com', '$2a$10$w09uY4B0p9E6iXgU.Wv49eN5FpE2jKzXlS7F00cYb9oA5o/g5G6C2', 'Rahul Verma', 'STUDENT', 1);

-- 2. Departments
INSERT INTO `departments` (`id`, `code`, `name`, `hod`, `description`) VALUES
(1, 'CSE', 'Computer Science & Engineering', 'Dr. Mehta', 'Department of Computer Science'),
(2, 'ECE', 'Electronics & Communication', 'Dr. Iyer', 'Department of Electronics'),
(3, 'IT', 'Information Technology', 'Dr. Joshi', 'Department of IT');

-- 3. Courses
INSERT INTO `courses` (`id`, `course_code`, `name`, `duration_years`, `department_id`) VALUES
(1, 'CS101', 'B.Tech Computer Science & Engineering', 4, 1),
(2, 'EC101', 'B.Tech Electronics & Communication', 4, 2),
(3, 'IT101', 'B.Tech Information Technology', 4, 3);

-- 4. Teachers
INSERT INTO `teachers` (`id`, `teacher_code`, `first_name`, `last_name`, `email`, `phone`, `designation`, `qualification`, `department_id`, `user_id`) VALUES
(1, 'TCH001', 'Priya', 'Sharma', 'teacher@student360.com', '9876500001', 'Associate Professor', 'Ph.D Computer Science', 1, 2),
(2, 'TCH002', 'Rajesh', 'Gupta', 'rajesh.gupta@student360.com', '9876500002', 'Professor & Dean', 'Ph.D Electronics', 2, 3),
(3, 'TCH003', 'Sunita', 'Deshmukh', 'sunita.d@student360.com', '9876500003', 'Assistant Professor', 'M.Tech Software Systems', 3, 4);

-- 5. Subjects
INSERT INTO `subjects` (`id`, `code`, `name`, `credits`, `semester`, `course_id`, `teacher_id`) VALUES
(1, 'CS301', 'Data Structures & Algorithms', 4, 3, 1, 1),
(2, 'CS302', 'Database Management Systems', 4, 3, 1, 1),
(3, 'CS401', 'Operating Systems', 4, 4, 1, 1),
(4, 'EC201', 'Digital Electronics', 4, 2, 2, 2),
(5, 'IT301', 'Web Technologies', 3, 3, 3, 3);

-- 6. Students
INSERT INTO `students` (`id`, `student_code`, `roll_number`, `first_name`, `last_name`, `email`, `phone`, `year`, `semester`, `status`, `admission_date`, `department_id`, `course_id`, `user_id`) VALUES
(1, 'STU001', 'R1001', 'Rahul', 'Verma', 'student@student360.com', '9876543201', 3, 5, 'ACTIVE', '2023-08-01', 1, 1, 5),
(2, 'STU002', 'R1002', 'Diya', 'Singh', 'student2@college.edu', '9876543202', 2, 3, 'ACTIVE', '2024-08-01', 1, 1, NULL);

-- 7. Marks
INSERT INTO `marks` (`id`, `student_id`, `subject_id`, `internal`, `assignment`, `midterm`, `end_sem`) VALUES
(1, 1, 1, 18, 17, 26, 25),
(2, 1, 2, 19, 18, 27, 28);

-- 8. Fees
INSERT INTO `fees` (`id`, `student_id`, `academic_year`, `total_fees`, `paid_amount`, `due_date`, `remarks`) VALUES
(1, 1, '2025-2026', 85000.0, 85000.0, '2026-06-30', 'Tuition & Lab fees paid in full'),
(2, 2, '2025-2026', 85000.0, 50000.0, '2026-05-15', 'First installment paid');

-- 9. Notices
INSERT INTO `notices` (`id`, `title`, `content`, `priority`, `target_audience`, `date`, `created_at`, `created_by_id`) VALUES
(1, 'Mid-Semester Examination Schedule', 'The mid-semester examinations commence on the 20th. Check notices for the date-sheet.', 'URGENT', 'ALL', CURDATE(), NOW(), 1),
(2, 'Google & Microsoft Campus Drive', 'Registration is now open for third & final year students.', 'IMPORTANT', 'STUDENTS', CURDATE(), NOW(), 2);

-- 10. Timetable
INSERT INTO `timetable` (`id`, `day`, `start_time`, `end_time`, `room`, `subject_id`, `teacher_id`) VALUES
(1, 'MONDAY', '09:00:00', '10:00:00', 'Room 301', 1, 1),
(2, 'MONDAY', '10:15:00', '11:15:00', 'Lab 2', 2, 1),
(3, 'TUESDAY', '09:00:00', '10:00:00', 'Room 301', 1, 1);
