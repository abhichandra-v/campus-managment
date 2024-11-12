package com.campus.filter;

import com.campus.model.Role;
import com.campus.model.User;
import com.campus.util.Attributes;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Single point of enforcement for the role-scoped paths (/student/*, /faculty/*,
 * /admin/*): requires a logged-in user, and requires their role to match the
 * path prefix. Individual servlets under these paths do not re-check the role.
 */
@WebFilter({"/student/*", "/faculty/*", "/admin/*"})
public class AuthorizationFilter implements jakarta.servlet.Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = session != null ? (User) session.getAttribute(Attributes.SESSION_USER) : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getRequestURI().substring(req.getContextPath().length());
        Role requiredRole = requiredRoleFor(path);
        if (requiredRole != null && user.getRole() != requiredRole) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to access this page.");
            return;
        }

        chain.doFilter(request, response);
    }

    private Role requiredRoleFor(String path) {
        if (path.startsWith("/student/")) {
            return Role.STUDENT;
        }
        if (path.startsWith("/faculty/")) {
            return Role.FACULTY;
        }
        if (path.startsWith("/admin/")) {
            return Role.ADMIN;
        }
        return null;
    }
}
