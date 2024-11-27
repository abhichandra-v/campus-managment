package com.campus.controller;

import com.campus.model.Grade;
import com.campus.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "StudentGradeServlet", urlPatterns = {"/student/grades"})
public class StudentGradeServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        List<Grade> grades = services().gradeService().findByStudentId(user.getId());
        req.setAttribute("pageTitle", "My Grades");
        req.setAttribute("grades", grades);
        req.getRequestDispatcher("/WEB-INF/jsp/student/grades.jsp").forward(req, resp);
    }
}
