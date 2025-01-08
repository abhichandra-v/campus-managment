package com.campus.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.campus.dao.CourseDao;
import com.campus.dao.EnrollmentDao;
import com.campus.dao.GradeDao;
import com.campus.model.Course;
import com.campus.model.Enrollment;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GradeServiceTest {

    private GradeDao gradeDao;
    private EnrollmentDao enrollmentDao;
    private CourseDao courseDao;
    private GradeService gradeService;

    private static final long ENROLLMENT_ID = 5L;
    private static final long COURSE_ID = 10L;
    private static final long OWNING_FACULTY_ID = 1L;

    @BeforeEach
    void setUp() {
        gradeDao = mock(GradeDao.class);
        enrollmentDao = mock(EnrollmentDao.class);
        courseDao = mock(CourseDao.class);
        gradeService = new GradeService(gradeDao, enrollmentDao, courseDao);

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
    void recordGradeSucceedsForAValidGradeFromTheOwningFaculty() {
        gradeService.recordGrade(ENROLLMENT_ID, "A", OWNING_FACULTY_ID);
        verify(gradeDao).upsert(ENROLLMENT_ID, "A");
    }

    @Test
    void recordGradeUpdatesAnExistingGradeViaUpsert() {
        gradeService.recordGrade(ENROLLMENT_ID, "B+", OWNING_FACULTY_ID);
        gradeService.recordGrade(ENROLLMENT_ID, "A-", OWNING_FACULTY_ID);
        verify(gradeDao).upsert(ENROLLMENT_ID, "B+");
        verify(gradeDao).upsert(ENROLLMENT_ID, "A-");
    }

    @Test
    void recordGradeRejectsAnUnrecognizedGradeValue() {
        assertThrows(ValidationException.class,
                () -> gradeService.recordGrade(ENROLLMENT_ID, "Z+", OWNING_FACULTY_ID));
        verify(gradeDao, never()).upsert(ENROLLMENT_ID, "Z+");
    }

    @Test
    void recordGradeRejectsANullGradeValue() {
        assertThrows(ValidationException.class,
                () -> gradeService.recordGrade(ENROLLMENT_ID, null, OWNING_FACULTY_ID));
    }

    @Test
    void recordGradeRejectsAFacultyMemberNotAssignedToTheCourse() {
        long someoneElsesFacultyId = 999L;
        assertThrows(UnauthorizedActionException.class,
                () -> gradeService.recordGrade(ENROLLMENT_ID, "A", someoneElsesFacultyId));
        verify(gradeDao, never()).upsert(ENROLLMENT_ID, "A");
    }
}
