package com.campus.controller;

import com.campus.model.Course;
import com.campus.model.User;
import com.campus.service.ValidationException;
import com.campus.util.AppConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Lets a student browse and enroll in courses open for the current semester. */
@WebServlet(name = "StudentCourseServlet", urlPatterns = {"/student/courses"})
public class StudentCourseServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        List<Course> available = services().courseService()
                .findAvailableForStudent(user.getId(), AppConfig.CURRENT_SEMESTER);
        req.setAttribute("pageTitle", "Enroll in Courses");
        req.setAttribute("semester", AppConfig.CURRENT_SEMESTER);
        req.setAttribute("courses", available);
        req.getRequestDispatcher("/WEB-INF/jsp/student/courses.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        long courseId;
        try {
            courseId = Long.parseLong(req.getParameter("courseId"));
        } catch (NumberFormatException e) {
            flashError(req, "Invalid course selection.");
            resp.sendRedirect(req.getContextPath() + "/student/courses");
            return;
        }
        try {
            var enrollment = services().enrollmentService().enroll(user.getId(), courseId);
            flashSuccess(req, "Enrolled in " + enrollment.getCourseCode() + " - " + enrollment.getCourseTitle() + ".");
        } catch (ValidationException e) {
            flashError(req, e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/student/courses");
    }
}
