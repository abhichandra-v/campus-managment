package com.campus.service;

import com.campus.dao.AttendanceDao;
import com.campus.dao.CourseDao;
import com.campus.dao.EnrollmentDao;
import com.campus.model.Attendance;
import com.campus.model.AttendanceStatus;
import com.campus.model.Course;
import com.campus.model.Enrollment;
import java.time.LocalDate;
import java.util.List;

/** Business rules for recording attendance: no future dates and course-ownership. */
public class AttendanceService {

    private final AttendanceDao attendanceDao;
    private final EnrollmentDao enrollmentDao;
    private final CourseDao courseDao;

    public AttendanceService(AttendanceDao attendanceDao, EnrollmentDao enrollmentDao, CourseDao courseDao) {
        this.attendanceDao = attendanceDao;
        this.enrollmentDao = enrollmentDao;
        this.courseDao = courseDao;
    }

    /**
     * Records or updates attendance for an enrollment on a given date.
     * {@code facultyId} must be the faculty member assigned to the enrollment's course.
     */
    public void recordAttendance(long enrollmentId, LocalDate date, AttendanceStatus status, long facultyId) {
        if (date == null) {
            throw new ValidationException("Attendance date is required.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException("Attendance cannot be recorded for a future date.");
        }
        Enrollment enrollment = enrollmentDao.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found: " + enrollmentId));
        Course course = courseDao.findById(enrollment.getCourseId())
                .orElseThrow(() -> new NotFoundException("Course not found: " + enrollment.getCourseId()));
        if (course.getFacultyId() == null || course.getFacultyId() != facultyId) {
            throw new UnauthorizedActionException("You are not assigned to " + course.getCode() + ".");
        }
        attendanceDao.upsert(enrollmentId, date, status);
    }

    public List<Attendance> findByEnrollmentId(long enrollmentId) {
        return attendanceDao.findByEnrollmentId(enrollmentId);
    }

    public List<Attendance> findByStudentId(long studentId) {
        return attendanceDao.findByStudentId(studentId);
    }
}
