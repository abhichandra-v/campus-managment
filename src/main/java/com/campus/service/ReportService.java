package com.campus.service;

import com.campus.dao.CourseDao;
import com.campus.dao.FacultyDao;
import com.campus.dao.StudentDao;
import com.campus.model.Course;
import java.util.List;

/** Read-only system-wide reports for the admin dashboard. */
public class ReportService {

    private final StudentDao studentDao;
    private final FacultyDao facultyDao;
    private final CourseDao courseDao;

    public ReportService(StudentDao studentDao, FacultyDao facultyDao, CourseDao courseDao) {
        this.studentDao = studentDao;
        this.facultyDao = facultyDao;
        this.courseDao = courseDao;
    }

    public SystemSummary getSystemSummary() {
        List<Course> courses = courseDao.findAll();
        int totalActiveEnrollments = courses.stream().mapToInt(Course::getEnrolledCount).sum();
        return new SystemSummary(studentDao.findAll().size(), facultyDao.findAll().size(),
                courses.size(), totalActiveEnrollments);
    }

    /** Per-course enrollment counts, optionally filtered to one semester. */
    public List<Course> getEnrollmentReport(String semester) {
        return (semester == null || semester.isBlank()) ? courseDao.findAll() : courseDao.findBySemester(semester);
    }
}
