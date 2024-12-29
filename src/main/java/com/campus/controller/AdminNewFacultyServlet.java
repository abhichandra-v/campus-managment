package com.campus.controller;

import com.campus.service.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminNewFacultyServlet", urlPatterns = {"/admin/users/new-faculty"})
public class AdminNewFacultyServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("pageTitle", "Create Faculty Account");
        req.getRequestDispatcher("/WEB-INF/jsp/admin/new-faculty.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String fullName = req.getParameter("fullName");
        String department = req.getParameter("department");
        String title = req.getParameter("title");
        String officeLocation = req.getParameter("officeLocation");

        try {
            var user = services().userManagementService()
                    .createFaculty(username, password, email, fullName, department, title, officeLocation);
            flashSuccess(req, "Faculty account '" + user.getUsername() + "' created.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        } catch (ValidationException e) {
            flashError(req, e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/users/new-faculty");
        }
    }
}
