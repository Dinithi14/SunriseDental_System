package com.sunrisedental.controller;

import com.google.gson.Gson;
import com.sunrisedental.model.Patient;
import com.sunrisedental.service.PatientService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "PatientServlet", urlPatterns = {"/patients", "/api/patients"})
public class PatientServlet extends HttpServlet {

    private final PatientService patientService = new PatientService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String format = req.getParameter("format");
        String query = req.getParameter("query");

        List<Patient> list = (query != null && !query.trim().isEmpty())
                ? patientService.searchPatients(query)
                : patientService.getAllPatients();

        if ("json".equals(format) || req.getServletPath().startsWith("/api/")) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(list));
            return;
        }

        req.setAttribute("patients", list);
        req.getRequestDispatcher("/patients.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        boolean isAjax = "true".equalsIgnoreCase(req.getParameter("ajax")) || req.getHeader("X-Requested-With") != null;

        try {
            if ("create".equals(action) || "register".equals(action)) {
                Patient p = new Patient();
                p.setFullName(req.getParameter("fullName"));
                p.setNicPassport(req.getParameter("nicPassport"));
                p.setContactNumber(req.getParameter("contactNumber"));
                p.setEmail(req.getParameter("email"));
                p.setAddress(req.getParameter("address"));
                String dob = req.getParameter("dateOfBirth");
                if (dob != null && !dob.trim().isEmpty()) {
                    p.setDateOfBirth(Date.valueOf(dob));
                }
                p.setGender(req.getParameter("gender"));
                p.setBloodGroup(req.getParameter("bloodGroup"));
                p.setEmergencyContact(req.getParameter("emergencyContact"));
                p.setMedicalHistory(req.getParameter("medicalHistory"));

                int id = patientService.registerPatient(p);

                if (isAjax) {
                    resp.setContentType("application/json");
                    Map<String, Object> res = new HashMap<>();
                    res.put("success", true);
                    res.put("id", id);
                    res.put("patient", p);
                    resp.getWriter().write(gson.toJson(res));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/patients?success=created");
                }
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("patientId"));
                boolean deleted = patientService.deletePatient(id);

                if (isAjax) {
                    resp.setContentType("application/json");
                    Map<String, Object> res = new HashMap<>();
                    res.put("success", deleted);
                    resp.getWriter().write(gson.toJson(res));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/patients?success=deleted");
                }
            }
        } catch (Exception e) {
            if (isAjax) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                Map<String, Object> res = new HashMap<>();
                res.put("success", false);
                res.put("error", e.getMessage());
                resp.getWriter().write(gson.toJson(res));
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                req.setAttribute("patients", patientService.getAllPatients());
                req.getRequestDispatcher("/patients.jsp").forward(req, resp);
            }
        }
    }
}
