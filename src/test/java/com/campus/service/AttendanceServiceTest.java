package com.campus.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.campus.dao.AttendanceDao;
import com.campus.dao.CourseDao;
import com.campus.dao.EnrollmentDao;
import com.campus.model.AttendanceStatus;
import com.campus.model.Course;
import com.campus.model.Enrollment;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttendanceServiceTest {

    private AttendanceDao attendanceDao;
    private EnrollmentDao enrollmentDao;
    private CourseDao courseDao;
    private AttendanceService attendanceService;

    private static final long ENROLLMENT_ID = 5L;
    private static final long COURSE_ID = 10L;
    private static final long OWNING_FACULTY_ID = 1L;

    @BeforeEach
    void setUp() {
        attendanceDao = mock(AttendanceDao.class);
        enrollmentDao = mock(EnrollmentDao.class);
        courseDao = mock(CourseDao.class);
        attendanceService = new AttendanceService(attendanceDao, enrollmentDao, courseDao);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(ENROLLMENT_ID);
        enrollment.setCourseId(COURSE_ID);
        when(enrollmentDao.findById(ENROLLMENT_ID)).thenReturn(Optional.of(enrollment));

        Course course = new Course();
        course.setId(COURSE_ID);
        course.setCode("CS101");
        course.setFacultyId(OWNING_FACULTY_ID);
        when(courseDao.findById(COURSE_ID)).thenReturn(Optional.of(course));
    }

    @Test
    void recordAttendanceSucceedsForAPastOrTodayDate() {
        attendanceService.recordAttendance(ENROLLMENT_ID, LocalDate.now(), AttendanceStatus.PRESENT,
                OWNING_FACULTY_ID);
        verify(attendanceDao).upsert(ENROLLMENT_ID, LocalDate.now(), AttendanceStatus.PRESENT);
    }

    @Test
    void recordAttendanceUpdatesAnExistingRecordViaUpsert() {
        LocalDate date = LocalDate.now().minusDays(1);
        attendanceService.recordAttendance(ENROLLMENT_ID, date, AttendanceStatus.ABSENT, OWNING_FACULTY_ID);
        attendanceService.recordAttendance(ENROLLMENT_ID, date, AttendanceStatus.LATE, OWNING_FACULTY_ID);
        verify(attendanceDao).upsert(ENROLLMENT_ID, date, AttendanceStatus.ABSENT);
        verify(attendanceDao).upsert(ENROLLMENT_ID, date, AttendanceStatus.LATE);
    }

    @Test
    void recordAttendanceRejectsAFutureDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertThrows(ValidationException.class,
                () -> attendanceService.recordAttendance(ENROLLMENT_ID, tomorrow, AttendanceStatus.PRESENT,
                        OWNING_FACULTY_ID));
        verify(attendanceDao, never()).upsert(ENROLLMENT_ID, tomorrow, AttendanceStatus.PRESENT);
    }

    @Test
    void recordAttendanceRejectsANullDate() {
        assertThrows(ValidationException.class,
                () -> attendanceService.recordAttendance(ENROLLMENT_ID, null, AttendanceStatus.PRESENT,
                        OWNING_FACULTY_ID));
    }

    @Test
    void recordAttendanceRejectsAFacultyMemberNotAssignedToTheCourse() {
        long someoneElsesFacultyId = 999L;
        assertThrows(UnauthorizedActionException.class,
                () -> attendanceService.recordAttendance(ENROLLMENT_ID, LocalDate.now(), AttendanceStatus.PRESENT,
                        someoneElsesFacultyId));
        verify(attendanceDao, never()).upsert(ENROLLMENT_ID, LocalDate.now(), AttendanceStatus.PRESENT);
    }
}
