package com.campus.controller;

import com.campus.model.EnrollmentStatus;
import com.campus.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "StudentDashboardServlet", urlPatterns = {"/student/dashboard"})
public class StudentDashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        long activeCourseCount = services().enrollmentService().findByStudentId(user.getId()).stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .count();
        req.setAttribute("pageTitle", "Student Dashboard");
        req.setAttribute("activeCourseCount", activeCourseCount);
        req.getRequestDispatcher("/WEB-INF/jsp/student/dashboard.jsp").forward(req, resp);
    }
}
