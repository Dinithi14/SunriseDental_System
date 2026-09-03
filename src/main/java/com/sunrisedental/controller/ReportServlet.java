package com.sunrisedental.controller;

import com.google.gson.Gson;
import com.sunrisedental.dto.ReportSummaryDTO;
import com.sunrisedental.service.ReportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "ReportServlet", urlPatterns = {"/reports", "/api/reports"})
public class ReportServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ReportSummaryDTO summary = reportService.getManagementSummary();

        String format = req.getParameter("format");
        if ("json".equals(format) || req.getServletPath().startsWith("/api/")) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(summary));
            return;
        }

        req.setAttribute("report", summary);
        req.getRequestDispatcher("/reports.jsp").forward(req, resp);
    }
}
