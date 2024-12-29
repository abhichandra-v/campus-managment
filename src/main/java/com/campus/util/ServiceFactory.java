package com.campus.util;

import com.campus.dao.AttendanceDao;
import com.campus.dao.CourseDao;
import com.campus.dao.EnrollmentDao;
import com.campus.dao.FacultyDao;
import com.campus.dao.GradeDao;
import com.campus.dao.JdbcAttendanceDao;
import com.campus.dao.JdbcCourseDao;
import com.campus.dao.JdbcEnrollmentDao;
import com.campus.dao.JdbcFacultyDao;
import com.campus.dao.JdbcGradeDao;
import com.campus.dao.JdbcStudentDao;
import com.campus.dao.JdbcUserDao;
import com.campus.dao.StudentDao;
import com.campus.dao.UserDao;
import com.campus.service.AttendanceService;
import com.campus.service.AuthService;
import com.campus.service.CourseService;
import com.campus.service.EnrollmentService;
import com.campus.service.GradeService;
import com.campus.service.ProfileService;
import com.campus.service.ReportService;
import com.campus.service.StudentService;
import com.campus.service.UserManagementService;
import jakarta.servlet.ServletContext;
import javax.sql.DataSource;

/**
 * Manual composition root: builds every DAO and service once per application
 * lifetime and hands them out to controllers, instead of each servlet wiring
 * up its own DAOs (or a full DI framework, which this project intentionally
 * avoids to keep the stack a plain Servlet/JSP/JDBC application).
 */
public final class ServiceFactory {

    private static final String ATTRIBUTE_NAME = "serviceFactory";

    private final UserDao userDao;
    private final StudentDao studentDao;
    private final FacultyDao facultyDao;
    private final CourseDao courseDao;
    private final EnrollmentDao enrollmentDao;
    private final GradeDao gradeDao;
    private final AttendanceDao attendanceDao;

    private final AuthService authService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final AttendanceService attendanceService;
    private final StudentService studentService;
    private final ProfileService profileService;
    private final UserManagementService userManagementService;
    private final ReportService reportService;

    private ServiceFactory(DataSource dataSource) {
        this.userDao = new JdbcUserDao(dataSource);
        this.studentDao = new JdbcStudentDao(dataSource);
        this.facultyDao = new JdbcFacultyDao(dataSource);
        this.courseDao = new JdbcCourseDao(dataSource);
        this.enrollmentDao = new JdbcEnrollmentDao(dataSource);
        this.gradeDao = new JdbcGradeDao(dataSource);
        this.attendanceDao = new JdbcAttendanceDao(dataSource);

        this.authService = new AuthService(userDao);
        this.courseService = new CourseService(courseDao, facultyDao);
        this.enrollmentService = new EnrollmentService(enrollmentDao, courseDao);
        this.gradeService = new GradeService(gradeDao, enrollmentDao, courseDao);
        this.attendanceService = new AttendanceService(attendanceDao, enrollmentDao, courseDao);
        this.studentService = new StudentService(studentDao);
        this.profileService = new ProfileService(userDao);
        this.userManagementService = new UserManagementService(dataSource, userDao, studentDao, facultyDao);
        this.reportService = new ReportService(studentDao, facultyDao, courseDao);
    }

    /** Returns the application-scoped ServiceFactory, creating it on first use. */
    public static ServiceFactory get(ServletContext context) {
        ServiceFactory factory = (ServiceFactory) context.getAttribute(ATTRIBUTE_NAME);
        if (factory == null) {
            synchronized (ServiceFactory.class) {
                factory = (ServiceFactory) context.getAttribute(ATTRIBUTE_NAME);
                if (factory == null) {
                    DataSource dataSource = (DataSource) context.getAttribute(Attributes.DATA_SOURCE);
                    if (dataSource == null) {
                        throw new IllegalStateException(
                                "DataSource not found on ServletContext; AppContextListener may not have run.");
                    }
                    factory = new ServiceFactory(dataSource);
                    context.setAttribute(ATTRIBUTE_NAME, factory);
                }
            }
        }
        return factory;
    }

    public UserDao userDao() {
        return userDao;
    }

    public StudentDao studentDao() {
        return studentDao;
    }

    public FacultyDao facultyDao() {
        return facultyDao;
    }

    public CourseDao courseDao() {
        return courseDao;
    }

    public EnrollmentDao enrollmentDao() {
        return enrollmentDao;
    }

    public GradeDao gradeDao() {
        return gradeDao;
    }

    public AttendanceDao attendanceDao() {
        return attendanceDao;
    }

    public AuthService authService() {
        return authService;
    }

    public CourseService courseService() {
        return courseService;
    }

    public EnrollmentService enrollmentService() {
        return enrollmentService;
    }

    public GradeService gradeService() {
        return gradeService;
    }

    public AttendanceService attendanceService() {
        return attendanceService;
    }

    public StudentService studentService() {
        return studentService;
    }

    public ProfileService profileService() {
        return profileService;
    }

    public UserManagementService userManagementService() {
        return userManagementService;
    }

    public ReportService reportService() {
        return reportService;
    }
}
