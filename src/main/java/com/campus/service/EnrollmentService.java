package com.campus.service;

import com.campus.dao.CourseDao;
import com.campus.dao.EnrollmentDao;
import com.campus.model.Course;
import com.campus.model.Enrollment;
import com.campus.model.EnrollmentStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business rules for enrolling in and dropping courses: duplicate-enrollment
 * prevention, capacity enforcement, and ownership checks on drop.
 */
public class EnrollmentService {

    private final EnrollmentDao enrollmentDao;
    private final CourseDao courseDao;

    public EnrollmentService(EnrollmentDao enrollmentDao, CourseDao courseDao) {
        this.enrollmentDao = enrollmentDao;
        this.courseDao = courseDao;
    }

    /**
     * Enrolls a student in a course, re-activating a previously dropped
     * enrollment if one exists rather than inserting a second row (the
     * schema allows only one enrollment per student/course pair).
     */
    public Enrollment enroll(long studentId, long courseId) {
        Course course = courseDao.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

        Enrollment existing = enrollmentDao.findByStudentAndCourse(studentId, courseId).orElse(null);
        if (existing != null) {
            if (existing.getStatus() == EnrollmentStatus.ENROLLED) {
                throw new ValidationException("You are already enrolled in " + course.getCode() + ".");
            }
            if (existing.getStatus() == EnrollmentStatus.COMPLETED) {
                throw new ValidationException("You have already completed " + course.getCode() + ".");
            }
        }

        int activeCount = courseDao.countActiveEnrollments(courseId);
        if (activeCount >= course.getCapacity()) {
            throw new ValidationException(course.getCode() + " is at capacity (" + course.getCapacity() + ").");
        }

        long enrollmentId;
        if (existing != null) {
            enrollmentDao.updateStatus(existing.getId(), EnrollmentStatus.ENROLLED, null);
            enrollmentId = existing.getId();
        } else {
            enrollmentId = enrollmentDao.insert(studentId, courseId);
        }
        return enrollmentDao.findById(enrollmentId)
                .orElseThrow(() -> new IllegalStateException("Enrollment vanished immediately after write"));
    }

    /** Drops an enrollment, verifying it belongs to the requesting student and is currently active. */
    public void drop(long enrollmentId, long studentId) {
        Enrollment enrollment = enrollmentDao.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found: " + enrollmentId));

        if (!enrollment.getStudentId().equals(studentId)) {
            throw new UnauthorizedActionException("This enrollment does not belong to you.");
        }
        if (enrollment.getStatus() != EnrollmentStatus.ENROLLED) {
            throw new ValidationException("Only an active enrollment can be dropped.");
        }
        enrollmentDao.updateStatus(enrollmentId, EnrollmentStatus.DROPPED, LocalDateTime.now());
    }

    public List<Enrollment> findByStudentId(long studentId) {
        return enrollmentDao.findByStudentId(studentId);
    }

    public List<Enrollment> findRosterByCourseId(long courseId) {
        return enrollmentDao.findByCourseId(courseId);
    }

    public Enrollment findById(long enrollmentId) {
        return enrollmentDao.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found: " + enrollmentId));
    }
}
