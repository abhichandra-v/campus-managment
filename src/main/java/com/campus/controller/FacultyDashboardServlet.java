package com.campus.controller;

import com.campus.model.User;
import com.campus.util.AppConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "FacultyDashboardServlet", urlPatterns = {"/faculty/dashboard"})
public class FacultyDashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        long currentSemesterCourseCount = services().courseService().findByFacultyId(user.getId()).stream()
                .filter(c -> AppConfig.CURRENT_SEMESTER.equals(c.getSemester()))
                .count();
        req.setAttribute("pageTitle", "Faculty Dashboard");
        req.setAttribute("currentSemesterCourseCount", currentSemesterCourseCount);
        req.setAttribute("semester", AppConfig.CURRENT_SEMESTER);
        req.getRequestDispatcher("/WEB-INF/jsp/faculty/dashboard.jsp").forward(req, resp);
    }
}
