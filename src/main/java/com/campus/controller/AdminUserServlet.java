package com.campus.controller;

import com.campus.model.User;
import com.campus.service.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Admin: list all accounts and toggle active/deactivated status. */
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users"})
public class AdminUserServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<User> users = services().userManagementService().findAll();
        req.setAttribute("pageTitle", "Manage Users");
        req.setAttribute("users", users);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User admin = currentUser(req);
        long userId;
        boolean active;
        try {
            userId = Long.parseLong(req.getParameter("userId"));
            active = Boolean.parseBoolean(req.getParameter("active"));
        } catch (NumberFormatException | NullPointerException e) {
            flashError(req, "Invalid request.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }
        if (userId == admin.getId()) {
            flashError(req, "You cannot deactivate your own account.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }
        try {
            services().userManagementService().setActive(userId, active);
            flashSuccess(req, "Account " + (active ? "reactivated" : "deactivated") + ".");
        } catch (NotFoundException e) {
            flashError(req, e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}
