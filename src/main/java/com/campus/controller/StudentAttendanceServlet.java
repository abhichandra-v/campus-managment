package com.campus.controller;

import com.campus.model.Attendance;
import com.campus.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "StudentAttendanceServlet", urlPatterns = {"/student/attendance"})
public class StudentAttendanceServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        List<Attendance> records = services().attendanceService().findByStudentId(user.getId());
        req.setAttribute("pageTitle", "My Attendance");
        req.setAttribute("records", records);
        req.getRequestDispatcher("/WEB-INF/jsp/student/attendance.jsp").forward(req, resp);
    }
}
