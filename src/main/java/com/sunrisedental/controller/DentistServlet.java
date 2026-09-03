package com.sunrisedental.controller;

import com.google.gson.Gson;
import com.sunrisedental.model.Dentist;
import com.sunrisedental.service.DentistService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "DentistServlet", urlPatterns = {"/dentists", "/api/dentists"})
public class DentistServlet extends HttpServlet {

    private final DentistService dentistService = new DentistService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Dentist> dentists = dentistService.getActiveDentists();
        String format = req.getParameter("format");
        if ("json".equals(format) || req.getServletPath().startsWith("/api/")) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(dentists));
            return;
        }
        req.setAttribute("dentists", dentists);
        req.getRequestDispatcher("/dentists.jsp").forward(req, resp);
    }
}
