package com.campus.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.campus.dao.CourseDao;
import com.campus.dao.EnrollmentDao;
import com.campus.model.Course;
import com.campus.model.Enrollment;
import com.campus.model.EnrollmentStatus;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnrollmentServiceTest {

    private EnrollmentDao enrollmentDao;
    private CourseDao courseDao;
    private EnrollmentService enrollmentService;
    private Course course;

    @BeforeEach
    void setUp() {
        enrollmentDao = mock(EnrollmentDao.class);
        courseDao = mock(CourseDao.class);
        enrollmentService = new EnrollmentService(enrollmentDao, courseDao);

        course = new Course();
        course.setId(10L);
        course.setCode("CS101");
        course.setTitle("Intro to Programming");
        course.setCapacity(2);
        when(courseDao.findById(10L)).thenReturn(Optional.of(course));
    }

    @Test
    void enrollSucceedsWhenSeatsAreAvailableAndNotAlreadyEnrolled() {
        when(enrollmentDao.findByStudentAndCourse(1L, 10L)).thenReturn(Optional.empty());
        when(courseDao.countActiveEnrollments(10L)).thenReturn(0);
        when(enrollmentDao.insert(1L, 10L)).thenReturn(100L);
        Enrollment created = new Enrollment();
        created.setId(100L);
        created.setCourseCode("CS101");
        when(enrollmentDao.findById(100L)).thenReturn(Optional.of(created));

        Enrollment result = enrollmentService.enroll(1L, 10L);

        assertEquals(100L, result.getId());
        verify(enrollmentDao).insert(1L, 10L);
    }

    @Test
    void enrollRejectsADuplicateActiveEnrollment() {
        Enrollment existing = new Enrollment();
        existing.setId(55L);
        existing.setStatus(EnrollmentStatus.ENROLLED);
        when(enrollmentDao.findByStudentAndCourse(1L, 10L)).thenReturn(Optional.of(existing));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> enrollmentService.enroll(1L, 10L));
        assertTrue(ex.getMessage().contains("already enrolled"));
        verify(enrollmentDao, never()).insert(anyLong(), anyLong());
    }

    @Test
    void enrollRejectsWhenCourseIsAtCapacity() {
        when(enrollmentDao.findByStudentAndCourse(1L, 10L)).thenReturn(Optional.empty());
        when(courseDao.countActiveEnrollments(10L)).thenReturn(2); // capacity is 2

        ValidationException ex = assertThrows(ValidationException.class,
                () -> enrollmentService.enroll(1L, 10L));
        assertTrue(ex.getMessage().contains("capacity"));
        verify(enrollmentDao, never()).insert(anyLong(), anyLong());
    }

    @Test
    void enrollReactivatesAPreviouslyDroppedEnrollmentInsteadOfInsertingANewRow() {
        Enrollment dropped = new Enrollment();
        dropped.setId(77L);
        dropped.setStatus(EnrollmentStatus.DROPPED);
        when(enrollmentDao.findByStudentAndCourse(1L, 10L)).thenReturn(Optional.of(dropped));
        when(courseDao.countActiveEnrollments(10L)).thenReturn(0);
        Enrollment reactivated = new Enrollment();
        reactivated.setId(77L);
        reactivated.setStatus(EnrollmentStatus.ENROLLED);
        when(enrollmentDao.findById(77L)).thenReturn(Optional.of(reactivated));

        Enrollment result = enrollmentService.enroll(1L, 10L);

        assertEquals(EnrollmentStatus.ENROLLED, result.getStatus());
        verify(enrollmentDao, never()).insert(anyLong(), anyLong());
        verify(enrollmentDao).updateStatus(eq(77L), eq(EnrollmentStatus.ENROLLED), isNull());
    }

    @Test
    void dropSucceedsForTheOwningStudentsActiveEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(200L);
        enrollment.setStudentId(1L);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        when(enrollmentDao.findById(200L)).thenReturn(Optional.of(enrollment));

        enrollmentService.drop(200L, 1L);

        verify(enrollmentDao).updateStatus(eq(200L), eq(EnrollmentStatus.DROPPED), any());
    }

    @Test
    void dropRejectsAnEnrollmentBelongingToAnotherStudent() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(200L);
        enrollment.setStudentId(1L);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        when(enrollmentDao.findById(200L)).thenReturn(Optional.of(enrollment));

        assertThrows(UnauthorizedActionException.class, () -> enrollmentService.drop(200L, 2L));
        verify(enrollmentDao, never()).updateStatus(anyLong(), any(), any());
    }

    @Test
    void dropRejectsAnAlreadyDroppedEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(200L);
        enrollment.setStudentId(1L);
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        when(enrollmentDao.findById(200L)).thenReturn(Optional.of(enrollment));

        assertThrows(ValidationException.class, () -> enrollmentService.drop(200L, 1L));
    }
}
