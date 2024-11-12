package com.campus.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "FacultyDashboardServlet", urlPatterns = {"/faculty/dashboard"})
public class FacultyDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("pageTitle", "Faculty Dashboard");
        req.getRequestDispatcher("/WEB-INF/jsp/faculty/dashboard.jsp").forward(req, resp);
    }
}
