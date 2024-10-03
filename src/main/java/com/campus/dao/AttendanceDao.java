package com.campus.dao;

import com.campus.model.Attendance;
import com.campus.model.AttendanceStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceDao {

    Optional<Attendance> findByEnrollmentAndDate(long enrollmentId, LocalDate date);

    List<Attendance> findByEnrollmentId(long enrollmentId);

    List<Attendance> findByStudentId(long studentId);

    /** Inserts an attendance row for the enrollment/date if none exists yet, otherwise updates it. */
    void upsert(long enrollmentId, LocalDate date, AttendanceStatus status);
}
