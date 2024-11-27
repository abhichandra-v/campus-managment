package com.campus.controller;

import com.campus.model.User;
import com.campus.util.Attributes;
import com.campus.util.ServiceFactory;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Shared helpers for controllers: reading the logged-in user (the
 * AuthorizationFilter guarantees one is present on every protected path) and
 * setting a one-time flash message to show after a POST-redirect-GET.
 */
public abstract class BaseServlet extends HttpServlet {

    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null ? (User) session.getAttribute(Attributes.SESSION_USER) : null;
    }

    protected ServiceFactory services() {
        return ServiceFactory.get(getServletContext());
    }

    protected void flashSuccess(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashSuccess", message);
    }

    protected void flashError(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashError", message);
    }
}
