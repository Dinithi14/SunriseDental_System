package com.sunrisedental.controller;

import com.google.gson.Gson;
import com.sunrisedental.dto.AppointmentDetailDTO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Patient;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.DentistService;
import com.sunrisedental.service.PatientService;
import com.sunrisedental.service.TreatmentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AppointmentServlet", urlPatterns = {"/appointments", "/api/appointments"})
public class AppointmentServlet extends HttpServlet {

    private final AppointmentService appointmentService = new AppointmentService();
    private final PatientService patientService = new PatientService();
    private final DentistService dentistService = new DentistService();
    private final TreatmentService treatmentService = new TreatmentService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String format = req.getParameter("format");

        if ("getDetail".equals(action)) {
            String appNo = req.getParameter("appNo");
            AppointmentDetailDTO detail = appointmentService.getAppointmentByNumber(appNo);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(detail));
            return;
        }

        if ("search".equals(action) || "json".equals(format)) {
            String query = req.getParameter("query");
            String status = req.getParameter("status");
            String dentistIdStr = req.getParameter("dentistId");
            Integer dentistId = (dentistIdStr != null && !dentistIdStr.isEmpty()) ? Integer.parseInt(dentistIdStr) : null;

            List<AppointmentDetailDTO> list = appointmentService.searchAppointments(query, status, null, null, dentistId);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(list));
            return;
        }

        // Standard Page Load
        req.setAttribute("appointments", appointmentService.getAllAppointments());
        req.setAttribute("dentists", dentistService.getActiveDentists());
        req.setAttribute("treatments", treatmentService.getActiveTreatments());
        req.setAttribute("patients", patientService.getAllPatients());

        String view = req.getParameter("view");
        if ("search".equals(view)) {
            req.getRequestDispatcher("/search.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/appointments.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        boolean isAjax = "true".equalsIgnoreCase(req.getParameter("ajax")) || req.getHeader("X-Requested-With") != null;

        try {
            if ("book".equals(action)) {
                int patientId = req.getParameter("patientId") != null && !req.getParameter("patientId").isEmpty()
                        ? Integer.parseInt(req.getParameter("patientId")) : 0;
                int dentistId = Integer.parseInt(req.getParameter("dentistId"));
                int treatmentId = Integer.parseInt(req.getParameter("treatmentId"));
                String dateStr = req.getParameter("appointmentDate");
                String timeStr = req.getParameter("appointmentTime");
                if (timeStr != null && timeStr.length() == 5) timeStr += ":00"; // format HH:mm:ss
                String notes = req.getParameter("notes");

                Appointment appointment = new Appointment();
                appointment.setPatientId(patientId);
                appointment.setDentistId(dentistId);
                appointment.setTreatmentId(treatmentId);
                appointment.setAppointmentDate(Date.valueOf(dateStr));
                appointment.setAppointmentTime(Time.valueOf(timeStr));
                appointment.setStatus("SCHEDULED");
                appointment.setNotes(notes);

                HttpSession session = req.getSession(false);
                if (session != null && session.getAttribute("userId") != null) {
                    appointment.setCreatedBy((Integer) session.getAttribute("userId"));
                }

                // If registering a brand new patient inline
                Patient newPatient = null;
                if (patientId <= 0) {
                    newPatient = new Patient();
                    newPatient.setFullName(req.getParameter("newPatientName"));
                    newPatient.setContactNumber(req.getParameter("newPatientContact"));
                    newPatient.setEmail(req.getParameter("newPatientEmail"));
                    newPatient.setAddress(req.getParameter("newPatientAddress"));
                    newPatient.setGender(req.getParameter("newPatientGender"));
                    newPatient.setMedicalHistory(req.getParameter("newPatientMedicalHistory"));
                }

                AppointmentDetailDTO booked = appointmentService.bookAppointment(appointment, newPatient);

                if (isAjax) {
                    resp.setContentType("application/json");
                    Map<String, Object> res = new HashMap<>();
                    res.put("success", true);
                    res.put("message", "Appointment successfully booked!");
                    res.put("appointment", booked);
                    resp.getWriter().write(gson.toJson(res));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/appointments?success=booked&appNo=" + booked.getAppointmentNumber());
                }

            } else if ("updateStatus".equals(action)) {
                int apptId = Integer.parseInt(req.getParameter("appointmentId"));
                String newStatus = req.getParameter("status");
                boolean success = appointmentService.updateAppointmentStatus(apptId, newStatus);

                if (isAjax) {
                    resp.setContentType("application/json");
                    Map<String, Object> res = new HashMap<>();
                    res.put("success", success);
                    res.put("message", success ? "Status updated successfully" : "Failed to update status");
                    resp.getWriter().write(gson.toJson(res));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/appointments?success=status_updated");
                }

            } else if ("delete".equals(action)) {
                int apptId = Integer.parseInt(req.getParameter("appointmentId"));
                boolean success = appointmentService.deleteAppointment(apptId);

                if (isAjax) {
                    resp.setContentType("application/json");
                    Map<String, Object> res = new HashMap<>();
                    res.put("success", success);
                    resp.getWriter().write(gson.toJson(res));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/appointments?success=deleted");
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
                req.setAttribute("appointments", appointmentService.getAllAppointments());
                req.setAttribute("dentists", dentistService.getActiveDentists());
                req.setAttribute("treatments", treatmentService.getActiveTreatments());
                req.setAttribute("patients", patientService.getAllPatients());
                req.getRequestDispatcher("/appointments.jsp").forward(req, resp);
            }
        }
    }
}
