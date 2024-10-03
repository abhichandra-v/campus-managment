package com.campus.dao;

import com.campus.model.Enrollment;
import com.campus.model.EnrollmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EnrollmentDao {

    Optional<Enrollment> findById(long id);

    Optional<Enrollment> findByStudentAndCourse(long studentId, long courseId);

    List<Enrollment> findByStudentId(long studentId);

    List<Enrollment> findByCourseId(long courseId);

    long insert(long studentId, long courseId);

    void updateStatus(long enrollmentId, EnrollmentStatus status, LocalDateTime droppedAt);

    int countActiveByCourseId(long courseId);
}
