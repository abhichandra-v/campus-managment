package com.campus.controller;

import com.campus.model.Course;
import com.campus.service.NotFoundException;
import com.campus.service.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin: edit an existing course's fields and faculty assignment. */
@WebServlet(name = "AdminCourseEditServlet", urlPatterns = {"/admin/courses/edit"})
public class AdminCourseEditServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long courseId;
        try {
            courseId = Long.parseLong(req.getParameter("courseId"));
        } catch (NumberFormatException | NullPointerException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "A courseId is required.");
            return;
        }
        Course course;
        try {
            course = services().courseService().getById(courseId);
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            return;
        }
        req.setAttribute("pageTitle", "Edit Course - " + course.getCode());
        req.setAttribute("course", course);
        req.setAttribute("facultyList", services().facultyDao().findAll());
        req.getRequestDispatcher("/WEB-INF/jsp/admin/course-edit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long courseId;
        try {
            courseId = Long.parseLong(req.getParameter("courseId"));
        } catch (NumberFormatException | NullPointerException e) {
            flashError(req, "Invalid request.");
            resp.sendRedirect(req.getContextPath() + "/admin/courses");
            return;
        }
        String code = req.getParameter("code");
        String title = req.getParameter("title");
        String semester = req.getParameter("semester");
        String facultyIdParam = req.getParameter("facultyId");

        try {
            int credits = Integer.parseInt(req.getParameter("credits"));
            int capacity = Integer.parseInt(req.getParameter("capacity"));
            services().courseService().updateCourse(courseId, code, title, credits, capacity, semester);
            Long facultyId = (facultyIdParam == null || facultyIdParam.isBlank())
                    ? null : Long.parseLong(facultyIdParam);
            services().courseService().assignFaculty(courseId, facultyId);
            flashSuccess(req, "Course updated.");
            resp.sendRedirect(req.getContextPath() + "/admin/courses");
        } catch (ValidationException | NotFoundException e) {
            flashError(req, e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/courses/edit?courseId=" + courseId);
        } catch (NumberFormatException e) {
            flashError(req, "Credits and capacity must be whole numbers.");
            resp.sendRedirect(req.getContextPath() + "/admin/courses/edit?courseId=" + courseId);
        }
    }
}
