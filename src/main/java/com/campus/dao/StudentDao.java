package com.campus.dao;

import com.campus.model.Student;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentDao {

    Optional<Student> findByUserId(long userId);

    Optional<Student> findByStudentNumber(String studentNumber);

    List<Student> findAll();

    void insert(long userId, String studentNumber, int enrollmentYear, String major, LocalDate dateOfBirth);

    void updateProfile(long userId, String major, LocalDate dateOfBirth);

    boolean existsByStudentNumber(String studentNumber);
}
