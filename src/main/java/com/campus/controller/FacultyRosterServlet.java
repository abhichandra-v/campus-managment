package com.campus.controller;

import com.campus.model.Course;
import com.campus.model.Enrollment;
import com.campus.model.Grade;
import com.campus.model.User;
import com.campus.service.NotFoundException;
import com.campus.service.UnauthorizedActionException;
import com.campus.service.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Faculty view of a course roster, with inline grade entry. */
@WebServlet(name = "FacultyRosterServlet", urlPatterns = {"/faculty/roster"})
public class FacultyRosterServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        long courseId;
        try {
            courseId = Long.parseLong(req.getParameter("courseId"));
        } catch (NumberFormatException | NullPointerException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "A courseId is required.");
            return;
        }

        Course course;
        try {
            course = requireOwnedCourse(courseId, user.getId());
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            return;
        } catch (UnauthorizedActionException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }

        List<Enrollment> roster = services().enrollmentService().findRosterByCourseId(courseId);
        Map<Long, String> gradeByEnrollment = new HashMap<>();
        for (Enrollment e : roster) {
            Grade grade = services().gradeService().findByEnrollmentId(e.getId());
            if (grade != null && grade.getGrade() != null) {
                gradeByEnrollment.put(e.getId(), grade.getGrade());
            }
        }

        req.setAttribute("pageTitle", "Roster - " + course.getCode());
        req.setAttribute("course", course);
        req.setAttribute("roster", roster);
        req.setAttribute("gradeByEnrollment", gradeByEnrollment);
        req.getRequestDispatcher("/WEB-INF/jsp/faculty/roster.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        long courseId;
        long enrollmentId;
        try {
            courseId = Long.parseLong(req.getParameter("courseId"));
            enrollmentId = Long.parseLong(req.getParameter("enrollmentId"));
        } catch (NumberFormatException | NullPointerException e) {
            flashError(req, "Invalid request.");
            resp.sendRedirect(req.getContextPath() + "/faculty/courses");
            return;
        }
        String grade = req.getParameter("grade");
        try {
            services().gradeService().recordGrade(enrollmentId, grade, user.getId());
            flashSuccess(req, "Grade recorded.");
        } catch (NotFoundException | UnauthorizedActionException | ValidationException e) {
            flashError(req, e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/faculty/roster?courseId=" + courseId);
    }

    private Course requireOwnedCourse(long courseId, long facultyId) {
        Course course = services().courseService().getById(courseId);
        if (course.getFacultyId() == null || course.getFacultyId() != facultyId) {
            throw new UnauthorizedActionException("You are not assigned to this course.");
        }
        return course;
    }
}
