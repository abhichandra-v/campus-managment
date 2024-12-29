package com.campus.controller;

import com.campus.service.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet(name = "AdminNewStudentServlet", urlPatterns = {"/admin/users/new-student"})
public class AdminNewStudentServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("pageTitle", "Create Student Account");
        req.getRequestDispatcher("/WEB-INF/jsp/admin/new-student.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String fullName = req.getParameter("fullName");
        String studentNumber = req.getParameter("studentNumber");
        String major = req.getParameter("major");
        String dobParam = req.getParameter("dateOfBirth");
        String enrollmentYearParam = req.getParameter("enrollmentYear");

        try {
            int enrollmentYear = Integer.parseInt(enrollmentYearParam);
            LocalDate dob = (dobParam == null || dobParam.isBlank()) ? null : LocalDate.parse(dobParam);
            var user = services().userManagementService().createStudent(
                    username, password, email, fullName, studentNumber, enrollmentYear, major, dob);
            flashSuccess(req, "Student account '" + user.getUsername() + "' created.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        } catch (ValidationException e) {
            flashError(req, e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/users/new-student");
        } catch (NumberFormatException e) {
            flashError(req, "Enrollment year must be a valid year.");
            resp.sendRedirect(req.getContextPath() + "/admin/users/new-student");
        } catch (DateTimeParseException e) {
            flashError(req, "Invalid date of birth.");
            resp.sendRedirect(req.getContextPath() + "/admin/users/new-student");
        }
    }
}
