package com.sunrisedental.controller;

import com.google.gson.Gson;
import com.sunrisedental.model.Treatment;
import com.sunrisedental.service.TreatmentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "TreatmentServlet", urlPatterns = {"/treatments", "/api/treatments"})
public class TreatmentServlet extends HttpServlet {

    private final TreatmentService treatmentService = new TreatmentService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Treatment> list = treatmentService.getActiveTreatments();
        String format = req.getParameter("format");
        if ("json".equals(format) || req.getServletPath().startsWith("/api/")) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(list));
            return;
        }
        req.setAttribute("treatments", list);
        req.getRequestDispatcher("/treatments.jsp").forward(req, resp);
    }
}
