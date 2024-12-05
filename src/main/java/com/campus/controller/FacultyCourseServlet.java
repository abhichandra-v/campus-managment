package com.campus.controller;

import com.campus.model.Course;
import com.campus.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "FacultyCourseServlet", urlPatterns = {"/faculty/courses"})
public class FacultyCourseServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        List<Course> courses = services().courseService().findByFacultyId(user.getId());
        req.setAttribute("pageTitle", "My Courses");
        req.setAttribute("courses", courses);
        req.getRequestDispatcher("/WEB-INF/jsp/faculty/courses.jsp").forward(req, resp);
    }
}
