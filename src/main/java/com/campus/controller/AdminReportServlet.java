package com.campus.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminReportServlet", urlPatterns = {"/admin/reports"})
public class AdminReportServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String semester = req.getParameter("semester");
        req.setAttribute("pageTitle", "Reports");
        req.setAttribute("summary", services().reportService().getSystemSummary());
        req.setAttribute("courses", services().reportService().getEnrollmentReport(semester));
        req.setAttribute("semester", semester);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/reports.jsp").forward(req, resp);
    }
}
