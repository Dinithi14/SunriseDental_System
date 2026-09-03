package com.sunrisedental.controller;

import com.google.gson.Gson;
import com.sunrisedental.dto.AppointmentDetailDTO;
import com.sunrisedental.dto.BillReceiptDTO;
import com.sunrisedental.model.Bill;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.BillingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "BillingServlet", urlPatterns = {"/billing", "/api/billing"})
public class BillingServlet extends HttpServlet {

    private final BillingService billingService = new BillingService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("receipt".equals(action)) {
            String billIdStr = req.getParameter("billId");
            String apptIdStr = req.getParameter("appointmentId");

            BillReceiptDTO receipt = null;
            if (billIdStr != null && !billIdStr.isEmpty()) {
                receipt = billingService.getReceiptByBillId(Integer.parseInt(billIdStr));
            } else if (apptIdStr != null && !apptIdStr.isEmpty()) {
                receipt = billingService.getReceiptByAppointmentId(Integer.parseInt(apptIdStr));
            }

            if (receipt == null) {
                resp.sendRedirect(req.getContextPath() + "/billing?error=receipt_not_found");
                return;
            }

            req.setAttribute("receipt", receipt);
            req.getRequestDispatcher("/receipt.jsp").forward(req, resp);
            return;
        }

        // Standard Billing Page
        List<AppointmentDetailDTO> appointments = appointmentService.getAllAppointments();
        req.setAttribute("appointments", appointments);

        String apptNo = req.getParameter("appNo");
        if (apptNo != null && !apptNo.trim().isEmpty()) {
            AppointmentDetailDTO selected = appointmentService.getAppointmentByNumber(apptNo.trim());
            req.setAttribute("selectedAppointment", selected);
        }

        req.getRequestDispatcher("/billing.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        boolean isAjax = "true".equalsIgnoreCase(req.getParameter("ajax")) || req.getHeader("X-Requested-With") != null;

        try {
            if ("preview".equals(action)) {
                BigDecimal treatmentCost = new BigDecimal(req.getParameter("treatmentCost"));
                BigDecimal consultationFee = new BigDecimal(req.getParameter("consultationFee"));
                String addCharStr = req.getParameter("additionalCharges");
                BigDecimal additionalCharges = (addCharStr != null && !addCharStr.isEmpty()) ? new BigDecimal(addCharStr) : BigDecimal.ZERO;
                String strategyName = req.getParameter("strategy");

                Bill preview = billingService.calculateBillPreview(treatmentCost, consultationFee, additionalCharges, strategyName);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(gson.toJson(preview));
                return;

            } else if ("generate".equals(action)) {
                int appointmentId = Integer.parseInt(req.getParameter("appointmentId"));
                String addCharStr = req.getParameter("additionalCharges");
                BigDecimal additionalCharges = (addCharStr != null && !addCharStr.isEmpty()) ? new BigDecimal(addCharStr) : BigDecimal.ZERO;
                String strategyName = req.getParameter("strategy");
                String paymentMethod = req.getParameter("paymentMethod");
                String notes = req.getParameter("notes");

                BillReceiptDTO receipt = billingService.generateAndSaveBill(appointmentId, additionalCharges, strategyName, paymentMethod, notes);

                if (isAjax) {
                    resp.setContentType("application/json");
                    Map<String, Object> res = new HashMap<>();
                    res.put("success", true);
                    res.put("receipt", receipt);
                    resp.getWriter().write(gson.toJson(res));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/billing?action=receipt&billId=" + receipt.getBillNumber());
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
                req.getRequestDispatcher("/billing.jsp").forward(req, resp);
            }
        }
    }
}
