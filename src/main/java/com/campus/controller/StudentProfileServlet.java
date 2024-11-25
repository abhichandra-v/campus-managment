package com.campus.controller;

import com.campus.model.Student;
import com.campus.model.User;
import com.campus.service.ValidationException;
import com.campus.util.Attributes;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet(name = "StudentProfileServlet", urlPatterns = {"/student/profile"})
public class StudentProfileServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        Student student = services().studentService().getProfile(user.getId());
        req.setAttribute("pageTitle", "My Profile");
        req.setAttribute("student", student);
        req.getRequestDispatcher("/WEB-INF/jsp/student/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String major = req.getParameter("major");
        String dobParam = req.getParameter("dateOfBirth");

        try {
            LocalDate dob = null;
            if (dobParam != null && !dobParam.isBlank()) {
                dob = LocalDate.parse(dobParam);
            }
            services().profileService().updateAccountInfo(user.getId(), email, fullName);
            services().studentService().updateProfile(user.getId(), major, dob);

            User refreshed = services().userDao().findById(user.getId())
                    .orElseThrow(() -> new IllegalStateException("User vanished after profile update"));
            req.getSession().setAttribute(Attributes.SESSION_USER, refreshed);

            flashSuccess(req, "Profile updated.");
        } catch (ValidationException | DateTimeParseException e) {
            flashError(req, e instanceof DateTimeParseException ? "Invalid date of birth." : e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/student/profile");
    }
}
