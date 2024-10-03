package com.campus.dao;

import com.campus.model.Course;
import java.util.List;
import java.util.Optional;

public interface CourseDao {

    Optional<Course> findById(long id);

    List<Course> findAll();

    List<Course> findBySemester(String semester);

    List<Course> findByFacultyId(long facultyId);

    /** Courses in the given semester that the student is not already actively enrolled in. */
    List<Course> findAvailableForStudent(long studentId, String semester);

    long insert(Course course);

    void update(Course course);

    void assignFaculty(long courseId, Long facultyId);

    int countActiveEnrollments(long courseId);

    boolean existsByCodeAndSemester(String code, String semester);
}
