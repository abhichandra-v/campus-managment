package com.campus.service;

import com.campus.dao.CourseDao;
import com.campus.dao.EnrollmentDao;
import com.campus.dao.GradeDao;
import com.campus.model.Course;
import com.campus.model.Enrollment;
import com.campus.model.Grade;
import java.util.List;
import java.util.Set;

/** Business rules for recording and updating grades: valid grade values and course-ownership. */
public class GradeService {

    private static final Set<String> ALLOWED_GRADES =
            Set.of("A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F", "I", "W");

    private final GradeDao gradeDao;
    private final EnrollmentDao enrollmentDao;
    private final CourseDao courseDao;

    public GradeService(GradeDao gradeDao, EnrollmentDao enrollmentDao, CourseDao courseDao) {
        this.gradeDao = gradeDao;
        this.enrollmentDao = enrollmentDao;
        this.courseDao = courseDao;
    }

    /**
     * Records or updates the grade for an enrollment. {@code facultyId} must be the
     * faculty member assigned to the enrollment's course.
     */
    public void recordGrade(long enrollmentId, String grade, long facultyId) {
        if (grade == null || !ALLOWED_GRADES.contains(grade.trim())) {
            throw new ValidationException("'" + grade + "' is not a recognized grade value.");
        }
        Enrollment enrollment = enrollmentDao.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found: " + enrollmentId));
        Course course = courseDao.findById(enrollment.getCourseId())
                .orElseThrow(() -> new NotFoundException("Course not found: " + enrollment.getCourseId()));
        if (course.getFacultyId() == null || course.getFacultyId() != facultyId) {
            throw new UnauthorizedActionException("You are not assigned to " + course.getCode() + ".");
        }
        gradeDao.upsert(enrollmentId, grade.trim());
    }

    public List<Grade> findByStudentId(long studentId) {
        return gradeDao.findByStudentId(studentId);
    }

    public Grade findByEnrollmentId(long enrollmentId) {
        return gradeDao.findByEnrollmentId(enrollmentId).orElse(null);
    }
}
