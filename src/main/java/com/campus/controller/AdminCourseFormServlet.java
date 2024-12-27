package com.campus.controller;

import com.campus.service.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin: create a new course offering. */
@WebServlet(name = "AdminCourseFormServlet", urlPatterns = {"/admin/courses/new"})
public class AdminCourseFormServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("pageTitle", "Create Course");
        req.getRequestDispatcher("/WEB-INF/jsp/admin/course-new.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String code = req.getParameter("code");
        String title = req.getParameter("title");
        String semester = req.getParameter("semester");
        try {
            int credits = Integer.parseInt(req.getParameter("credits"));
            int capacity = Integer.parseInt(req.getParameter("capacity"));
            var course = services().courseService().createCourse(code, title, credits, capacity, semester);
            flashSuccess(req, "Course " + course.getCode() + " created.");
            resp.sendRedirect(req.getContextPath() + "/admin/courses");
        } catch (ValidationException e) {
            flashError(req, e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/courses/new");
        } catch (NumberFormatException e) {
            flashError(req, "Credits and capacity must be whole numbers.");
            resp.sendRedirect(req.getContextPath() + "/admin/courses/new");
        }
    }
}
