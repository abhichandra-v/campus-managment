package com.campus.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.campus.model.AttendanceStatus;
import com.campus.model.Course;
import com.campus.model.Role;
import com.campus.model.User;
import com.campus.testsupport.TestDatabase;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Exercises the grade and attendance upsert queries (MySQL's
 * ON DUPLICATE KEY UPDATE) against a real H2 database, confirming the
 * schema's unique constraints back the "insert once, update thereafter"
 * behavior the services rely on.
 */
class GradeAttendanceDaoIntegrationTest {

    private GradeDao gradeDao;
    private AttendanceDao attendanceDao;
    private long enrollmentId;

    @BeforeEach
    void setUp() {
        DataSource dataSource = TestDatabase.createDataSource();
        UserDao userDao = new JdbcUserDao(dataSource);
        StudentDao studentDao = new JdbcStudentDao(dataSource);
        CourseDao courseDao = new JdbcCourseDao(dataSource);
        EnrollmentDao enrollmentDao = new JdbcEnrollmentDao(dataSource);
        gradeDao = new JdbcGradeDao(dataSource);
        attendanceDao = new JdbcAttendanceDao(dataSource);

        User student = new User();
        student.setUsername("student1");
        student.setPasswordHash("x".repeat(60));
        student.setRole(Role.STUDENT);
        student.setEmail("student1@example.edu");
        student.setFullName("Student One");
        student.setActive(true);
        long studentId = userDao.insert(student);
        studentDao.insert(studentId, "S0001", 2024, "Computer Science", null);

        Course course = new Course();
        course.setCode("CS101");
        course.setTitle("Intro to Programming");
        course.setCredits(3);
        course.setCapacity(30);
        course.setSemester("FALL2024");
        long courseId = courseDao.insert(course);

        enrollmentId = enrollmentDao.insert(studentId, courseId);
    }

    @Test
    void gradeUpsertInsertsOnFirstCallAndUpdatesOnSubsequentCalls() {
        gradeDao.upsert(enrollmentId, "B+");
        assertEquals("B+", gradeDao.findByEnrollmentId(enrollmentId).orElseThrow().getGrade());

        gradeDao.upsert(enrollmentId, "A-");
        assertEquals("A-", gradeDao.findByEnrollmentId(enrollmentId).orElseThrow().getGrade());
    }

    @Test
    void attendanceUpsertInsertsOnFirstCallAndUpdatesOnSubsequentCallsForTheSameDate() {
        LocalDate date = LocalDate.of(2024, 9, 10);

        attendanceDao.upsert(enrollmentId, date, AttendanceStatus.PRESENT);
        assertEquals(AttendanceStatus.PRESENT,
                attendanceDao.findByEnrollmentAndDate(enrollmentId, date).orElseThrow().getStatus());

        attendanceDao.upsert(enrollmentId, date, AttendanceStatus.LATE);
        assertEquals(AttendanceStatus.LATE,
                attendanceDao.findByEnrollmentAndDate(enrollmentId, date).orElseThrow().getStatus());

        assertEquals(1, attendanceDao.findByEnrollmentId(enrollmentId).size(),
                "the second upsert should update the existing row, not add a second one");
    }

    @Test
    void attendanceOnDifferentDatesProducesSeparateRecords() {
        attendanceDao.upsert(enrollmentId, LocalDate.of(2024, 9, 10), AttendanceStatus.PRESENT);
        attendanceDao.upsert(enrollmentId, LocalDate.of(2024, 9, 11), AttendanceStatus.ABSENT);

        assertEquals(2, attendanceDao.findByEnrollmentId(enrollmentId).size());
    }
}
