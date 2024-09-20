package com.campus.model;

import java.time.LocalDateTime;

/**
 * Maps to the {@code enrollments} table. {@code studentName}, {@code courseCode}
 * and {@code courseTitle} are read-only projections populated by DAO joins for
 * display purposes; they are not persisted on this table.
 */
public class Enrollment {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
    private LocalDateTime droppedAt;

    public Enrollment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public LocalDateTime getDroppedAt() {
        return droppedAt;
    }

    public void setDroppedAt(LocalDateTime droppedAt) {
        this.droppedAt = droppedAt;
    }
}
