package com.campus.controller;

import com.campus.model.Role;
import com.campus.model.User;
import com.campus.service.AuthService;
import com.campus.service.AuthenticationException;
import com.campus.util.Attributes;
import com.campus.util.CsrfTokenUtil;
import com.campus.util.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession existing = req.getSession(false);
        if (existing != null && existing.getAttribute(Attributes.SESSION_USER) != null) {
            redirectToDashboard(req, resp, (User) existing.getAttribute(Attributes.SESSION_USER));
            return;
        }
        CsrfTokenUtil.getOrCreateToken(req.getSession());
        req.getRequestDispatcher("/WEB-INF/jsp/common/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        AuthService authService = ServiceFactory.get(getServletContext()).authService();
        try {
            User user = authService.authenticate(username, password);

            // Regenerate the session id on privilege change to prevent session fixation.
            HttpSession oldSession = req.getSession(false);
            String csrfToken = oldSession != null
                    ? (String) oldSession.getAttribute(Attributes.CSRF_TOKEN)
                    : null;
            req.changeSessionId();
            HttpSession session = req.getSession();
            session.setAttribute(Attributes.SESSION_USER, user);
            session.setAttribute(Attributes.CSRF_TOKEN,
                    csrfToken != null ? csrfToken : CsrfTokenUtil.getOrCreateToken(session));
            session.setMaxInactiveInterval(30 * 60);

            redirectToDashboard(req, resp, user);
        } catch (AuthenticationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("username", username);
            CsrfTokenUtil.getOrCreateToken(req.getSession());
            req.getRequestDispatcher("/WEB-INF/jsp/common/login.jsp").forward(req, resp);
        }
    }

    private void redirectToDashboard(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        String path = switch (user.getRole()) {
            case STUDENT -> "/student/dashboard";
            case FACULTY -> "/faculty/dashboard";
            case ADMIN -> "/admin/dashboard";
        };
        resp.sendRedirect(req.getContextPath() + path);
    }
}
