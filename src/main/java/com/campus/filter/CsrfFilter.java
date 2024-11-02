package com.campus.filter;

import com.campus.util.CsrfTokenUtil;
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
 * Rejects any POST/PUT/DELETE request whose {@code csrfToken} parameter does not
 * match the token stored in the caller's session. GET requests are read-only by
 * convention in this app and are not checked.
 */
@WebFilter("/*")
public class CsrfFilter implements jakarta.servlet.Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (isStateChanging(req.getMethod())) {
            HttpSession session = req.getSession(false);
            String submitted = req.getParameter("csrfToken");
            if (!CsrfTokenUtil.isValid(session, submitted)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Invalid or expired form token. Please go back and try again.");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isStateChanging(String method) {
        return "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method);
    }
}
