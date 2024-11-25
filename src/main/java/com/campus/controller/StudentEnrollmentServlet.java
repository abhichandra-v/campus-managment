package com.campus.controller;

import com.campus.model.Enrollment;
import com.campus.model.User;
import com.campus.service.NotFoundException;
import com.campus.service.UnauthorizedActionException;
import com.campus.service.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Lets a student view their enrollments and drop an active one. */
@WebServlet(name = "StudentEnrollmentServlet", urlPatterns = {"/student/enrollments"})
public class StudentEnrollmentServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        List<Enrollment> enrollments = services().enrollmentService().findByStudentId(user.getId());
        req.setAttribute("pageTitle", "My Courses");
        req.setAttribute("enrollments", enrollments);
        req.getRequestDispatcher("/WEB-INF/jsp/student/enrollments.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        long enrollmentId;
        try {
            enrollmentId = Long.parseLong(req.getParameter("enrollmentId"));
        } catch (NumberFormatException e) {
            flashError(req, "Invalid enrollment.");
            resp.sendRedirect(req.getContextPath() + "/student/enrollments");
            return;
        }
        try {
            services().enrollmentService().drop(enrollmentId, user.getId());
            flashSuccess(req, "Enrollment dropped.");
        } catch (NotFoundException | UnauthorizedActionException | ValidationException e) {
            flashError(req, e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/student/enrollments");
    }
}
