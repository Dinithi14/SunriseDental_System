package com.sunrisedental.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Allow static resources, login, and auth endpoints without login
        boolean isPublicResource = path.startsWith("/css/") ||
                                   path.startsWith("/js/") ||
                                   path.startsWith("/images/") ||
                                   path.equals("/login.jsp") ||
                                   path.equals("/auth") ||
                                   path.equals("/login");

        HttpSession session = req.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("username") != null);

        if (isLoggedIn || isPublicResource) {
            chain.doFilter(request, response);
        } else {
            // Clean redirect to login page for unauthorized access
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

    @Override
    public void destroy() {}
}
