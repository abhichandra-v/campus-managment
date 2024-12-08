package com.campus.controller;

import com.campus.model.Course;
import com.campus.model.User;
import com.campus.util.AppConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Shows the courses a faculty member is teaching in the current semester. */
@WebServlet(name = "FacultyScheduleServlet", urlPatterns = {"/faculty/schedule"})
public class FacultyScheduleServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        List<Course> courses = services().courseService().findByFacultyId(user.getId()).stream()
                .filter(c -> AppConfig.CURRENT_SEMESTER.equals(c.getSemester()))
                .toList();
        req.setAttribute("pageTitle", "My Schedule");
        req.setAttribute("semester", AppConfig.CURRENT_SEMESTER);
        req.setAttribute("courses", courses);
        req.getRequestDispatcher("/WEB-INF/jsp/faculty/schedule.jsp").forward(req, resp);
    }
}
