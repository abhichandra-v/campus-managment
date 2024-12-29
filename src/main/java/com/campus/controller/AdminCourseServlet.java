package com.campus.controller;

import com.campus.model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminCourseServlet", urlPatterns = {"/admin/courses"})
public class AdminCourseServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Course> courses = services().courseService().findAll();
        req.setAttribute("pageTitle", "Manage Courses");
        req.setAttribute("courses", courses);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/courses.jsp").forward(req, resp);
    }
}
