package com.campus.dao;

import com.campus.model.Grade;
import java.util.List;
import java.util.Optional;

public interface GradeDao {

    Optional<Grade> findByEnrollmentId(long enrollmentId);

    List<Grade> findByStudentId(long studentId);

    /** Inserts a grade row for the enrollment if none exists yet, otherwise updates it. */
    void upsert(long enrollmentId, String grade);
}
