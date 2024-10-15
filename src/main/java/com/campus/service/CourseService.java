package com.campus.service;

import com.campus.dao.CourseDao;
import com.campus.dao.FacultyDao;
import com.campus.model.Course;
import java.util.List;

/** Business rules for managing course offerings and faculty assignment. */
public class CourseService {

    private final CourseDao courseDao;
    private final FacultyDao facultyDao;

    public CourseService(CourseDao courseDao, FacultyDao facultyDao) {
        this.courseDao = courseDao;
        this.facultyDao = facultyDao;
    }

    public Course createCourse(String code, String title, int credits, int capacity, String semester) {
        validateCourseFields(code, title, credits, capacity, semester);
        if (courseDao.existsByCodeAndSemester(code, semester)) {
            throw new ValidationException(code + " is already offered in " + semester + ".");
        }
        Course course = new Course();
        course.setCode(code.trim());
        course.setTitle(title.trim());
        course.setCredits(credits);
        course.setCapacity(capacity);
        course.setSemester(semester.trim());
        long id = courseDao.insert(course);
        return getById(id);
    }

    public Course updateCourse(long courseId, String code, String title, int credits, int capacity,
            String semester) {
        validateCourseFields(code, title, credits, capacity, semester);
        Course course = getById(courseId);
        course.setCode(code.trim());
        course.setTitle(title.trim());
        course.setCredits(credits);
        course.setCapacity(capacity);
        course.setSemester(semester.trim());
        courseDao.update(course);
        return getById(courseId);
    }

    public void assignFaculty(long courseId, Long facultyId) {
        getById(courseId);
        if (facultyId != null) {
            facultyDao.findByUserId(facultyId)
                    .orElseThrow(() -> new NotFoundException("Faculty not found: " + facultyId));
        }
        courseDao.assignFaculty(courseId, facultyId);
    }

    public Course getById(long courseId) {
        return courseDao.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));
    }

    public List<Course> findAll() {
        return courseDao.findAll();
    }

    public List<Course> findBySemester(String semester) {
        return courseDao.findBySemester(semester);
    }

    public List<Course> findByFacultyId(long facultyId) {
        return courseDao.findByFacultyId(facultyId);
    }

    public List<Course> findAvailableForStudent(long studentId, String semester) {
        return courseDao.findAvailableForStudent(studentId, semester);
    }

    private void validateCourseFields(String code, String title, int credits, int capacity, String semester) {
        if (code == null || code.isBlank()) {
            throw new ValidationException("Course code is required.");
        }
        if (title == null || title.isBlank()) {
            throw new ValidationException("Course title is required.");
        }
        if (credits < 1 || credits > 6) {
            throw new ValidationException("Credits must be between 1 and 6.");
        }
        if (capacity < 1) {
            throw new ValidationException("Capacity must be at least 1.");
        }
        if (semester == null || semester.isBlank()) {
            throw new ValidationException("Semester is required.");
        }
    }
}
