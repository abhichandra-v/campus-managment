-- ============================================================================
-- Campus Management System - Initial Schema
-- Target: MySQL 8.0+
-- Normal form: 3NF (each non-key column depends on the key, the whole key,
-- and nothing but the key; role-specific attributes are split into their own
-- tables rather than kept nullable on a single wide "users" table).
-- ============================================================================

CREATE DATABASE IF NOT EXISTS campus_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus_management;

-- ----------------------------------------------------------------------------
-- users: one row per account, regardless of role. Role-specific attributes
-- live in students/faculty so this table never carries columns that are
-- meaningless for a given role (a partial-dependency / 3NF violation).
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL,
    password_hash   VARCHAR(60)     NOT NULL,           -- BCrypt hash, always 60 chars
    role            ENUM('STUDENT', 'FACULTY', 'ADMIN') NOT NULL,
    email           VARCHAR(100)    NOT NULL,
    full_name       VARCHAR(100)    NOT NULL,
    is_active       TINYINT(1)      NOT NULL DEFAULT 1,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE INDEX idx_users_role ON users (role);

-- ----------------------------------------------------------------------------
-- students: 1-to-1 extension of users for accounts with role = STUDENT.
-- ----------------------------------------------------------------------------
CREATE TABLE students (
    user_id             BIGINT UNSIGNED PRIMARY KEY,
    student_number      VARCHAR(20)  NOT NULL,
    enrollment_year     SMALLINT     NOT NULL,
    major               VARCHAR(100) NULL,
    date_of_birth       DATE         NULL,
    CONSTRAINT uq_students_student_number UNIQUE (student_number),
    CONSTRAINT fk_students_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- faculty: 1-to-1 extension of users for accounts with role = FACULTY.
-- ----------------------------------------------------------------------------
CREATE TABLE faculty (
    user_id         BIGINT UNSIGNED PRIMARY KEY,
    department      VARCHAR(100) NOT NULL,
    title           VARCHAR(50)  NULL,
    office_location VARCHAR(100) NULL,
    CONSTRAINT fk_faculty_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_faculty_department ON faculty (department);

-- ----------------------------------------------------------------------------
-- courses: a course offering for a given semester, optionally assigned to
-- one faculty member. A course is identified by (code, semester) so the same
-- catalog code can be re-offered in a later semester.
-- ----------------------------------------------------------------------------
CREATE TABLE courses (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL,
    title       VARCHAR(150) NOT NULL,
    credits     TINYINT UNSIGNED NOT NULL,
    faculty_id  BIGINT UNSIGNED NULL,
    capacity    INT UNSIGNED NOT NULL DEFAULT 30,
    semester    VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_courses_code_semester UNIQUE (code, semester),
    CONSTRAINT chk_courses_credits CHECK (credits BETWEEN 1 AND 6),
    CONSTRAINT chk_courses_capacity CHECK (capacity > 0),
    CONSTRAINT fk_courses_faculty
        FOREIGN KEY (faculty_id) REFERENCES faculty (user_id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_courses_faculty_id ON courses (faculty_id);
CREATE INDEX idx_courses_semester ON courses (semester);

-- ----------------------------------------------------------------------------
-- enrollments: the association between a student and a course offering.
-- ----------------------------------------------------------------------------
CREATE TABLE enrollments (
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id   BIGINT UNSIGNED NOT NULL,
    course_id    BIGINT UNSIGNED NOT NULL,
    status       ENUM('ENROLLED', 'DROPPED', 'COMPLETED') NOT NULL DEFAULT 'ENROLLED',
    enrolled_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dropped_at   TIMESTAMP NULL,
    CONSTRAINT uq_enrollments_student_course UNIQUE (student_id, course_id),
    CONSTRAINT fk_enrollments_student
        FOREIGN KEY (student_id) REFERENCES students (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_enrollments_course
        FOREIGN KEY (course_id) REFERENCES courses (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_enrollments_student_id ON enrollments (student_id);
CREATE INDEX idx_enrollments_course_id ON enrollments (course_id);
CREATE INDEX idx_enrollments_status ON enrollments (status);

-- ----------------------------------------------------------------------------
-- grades: at most one grade record per enrollment (1-to-1).
-- ----------------------------------------------------------------------------
CREATE TABLE grades (
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    enrollment_id  BIGINT UNSIGNED NOT NULL,
    grade          VARCHAR(2) NULL,
    graded_at      TIMESTAMP NULL,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_grades_enrollment UNIQUE (enrollment_id),
    CONSTRAINT chk_grades_value CHECK (
        grade IS NULL OR grade IN ('A','A-','B+','B','B-','C+','C','C-','D+','D','F','I','W')
    ),
    CONSTRAINT fk_grades_enrollment
        FOREIGN KEY (enrollment_id) REFERENCES enrollments (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- attendance: at most one attendance record per enrollment per calendar day.
-- ----------------------------------------------------------------------------
CREATE TABLE attendance (
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    enrollment_id    BIGINT UNSIGNED NOT NULL,
    attendance_date  DATE NOT NULL,
    status           ENUM('PRESENT', 'ABSENT', 'LATE', 'EXCUSED') NOT NULL,
    recorded_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_attendance_enrollment_date UNIQUE (enrollment_id, attendance_date),
    CONSTRAINT fk_attendance_enrollment
        FOREIGN KEY (enrollment_id) REFERENCES enrollments (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_attendance_enrollment_id ON attendance (enrollment_id);
CREATE INDEX idx_attendance_date ON attendance (attendance_date);
