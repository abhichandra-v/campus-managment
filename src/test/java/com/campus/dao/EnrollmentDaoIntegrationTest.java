package com.campus.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.campus.model.Course;
import com.campus.model.EnrollmentStatus;
import com.campus.model.Role;
import com.campus.model.User;
import com.campus.testsupport.TestDatabase;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Exercises JdbcEnrollmentDao (and, as fixtures, JdbcUserDao/JdbcStudentDao/
 * JdbcCourseDao) against a real H2 database loaded from the project's schema.
 */
class EnrollmentDaoIntegrationTest {

    private EnrollmentDao enrollmentDao;
    private CourseDao courseDao;
    private long studentId;
    private long courseId;

    @BeforeEach
    void setUp() {
        DataSource dataSource = TestDatabase.createDataSource();
        UserDao userDao = new JdbcUserDao(dataSource);
        StudentDao studentDao = new JdbcStudentDao(dataSource);
        courseDao = new JdbcCourseDao(dataSource);
        enrollmentDao = new JdbcEnrollmentDao(dataSource);

        User student = new User();
        student.setUsername("student1");
        student.setPasswordHash("x".repeat(60));
        student.setRole(Role.STUDENT);
        student.setEmail("student1@example.edu");
        student.setFullName("Student One");
        student.setActive(true);
        studentId = userDao.insert(student);
        studentDao.insert(studentId, "S0001", 2024, "Computer Science", null);

        Course course = new Course();
        course.setCode("CS101");
        course.setTitle("Intro to Programming");
        course.setCredits(3);
        course.setCapacity(30);
        course.setSemester("FALL2024");
        courseId = courseDao.insert(course);
    }

    @Test
    void insertThenFindByStudentAndCourseReturnsAnEnrolledStatus() {
        long id = enrollmentDao.insert(studentId, courseId);

        var found = enrollmentDao.findByStudentAndCourse(studentId, courseId).orElseThrow();
        assertEquals(id, found.getId());
        assertEquals(EnrollmentStatus.ENROLLED, found.getStatus());
        assertEquals("CS101", found.getCourseCode());
    }

    @Test
    void countActiveByCourseIdCountsOnlyEnrolledStatus() {
        enrollmentDao.insert(studentId, courseId);
        assertEquals(1, enrollmentDao.countActiveByCourseId(courseId));
        assertEquals(1, courseDao.countActiveEnrollments(courseId));
    }

    @Test
    void updateStatusToDroppedRemovesItFromTheActiveCount() {
        long id = enrollmentDao.insert(studentId, courseId);

        enrollmentDao.updateStatus(id, EnrollmentStatus.DROPPED, LocalDateTime.now());

        var found = enrollmentDao.findById(id).orElseThrow();
        assertEquals(EnrollmentStatus.DROPPED, found.getStatus());
        assertEquals(0, enrollmentDao.countActiveByCourseId(courseId));
    }

    @Test
    void theSchemaRejectsASecondEnrollmentRowForTheSameStudentAndCourse() {
        enrollmentDao.insert(studentId, courseId);

        // Defense in depth: even bypassing EnrollmentService, the DB's unique
        // constraint on (student_id, course_id) must still hold.
        assertThrows(DataAccessException.class, () -> enrollmentDao.insert(studentId, courseId));
    }

    @Test
    void findAvailableForStudentExcludesCoursesTheStudentIsActivelyEnrolledIn() {
        assertTrue(courseDao.findAvailableForStudent(studentId, "FALL2024").stream()
                .anyMatch(c -> c.getId().equals(courseId)));

        enrollmentDao.insert(studentId, courseId);

        assertTrue(courseDao.findAvailableForStudent(studentId, "FALL2024").stream()
                .noneMatch(c -> c.getId().equals(courseId)));
    }
}
