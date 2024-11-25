package com.campus.service;

import com.campus.dao.StudentDao;
import com.campus.model.Student;
import java.time.LocalDate;

/** Business rules for a student's own profile. */
public class StudentService {

    private final StudentDao studentDao;

    public StudentService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public Student getProfile(long userId) {
        return studentDao.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Student not found: " + userId));
    }

    public void updateProfile(long userId, String major, LocalDate dateOfBirth) {
        if (major != null && major.length() > 100) {
            throw new ValidationException("Major must be 100 characters or fewer.");
        }
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now().minusYears(10))) {
            throw new ValidationException("Please enter a valid date of birth.");
        }
        studentDao.updateProfile(userId, major, dateOfBirth);
    }
}
