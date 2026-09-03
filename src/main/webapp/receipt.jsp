<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.dto.BillReceiptDTO" %>

<jsp:include page="includes/header.jsp" />

<%
    BillReceiptDTO r = (BillReceiptDTO) request.getAttribute("receipt");
    if (r == null) {
        response.sendRedirect(request.getContextPath() + "/billing");
        return;
    }
%>

<div class="no-print" style="max-width: 800px; margin: 0 auto 1.5rem; display: flex; justify-content: space-between; align-items: center;">
    <a href="${pageContext.request.contextPath}/billing" class="btn btn-secondary">⬅ Back to Billing</a>
    <button onclick="window.print()" class="btn btn-primary" style="font-size: 1rem; padding: 0.75rem 1.5rem;">
        🖨️ Print Patient Bill / Receipt
    </button>
</div>

<!-- Official Printable Receipt Document -->
<div class="receipt-wrapper">
    <div class="receipt-header">
        <div class="receipt-clinic-info">
            <h2>✨ SUNRISE DENTAL CLINIC</h2>
            <p>No. 120, Galle Road, Colombo 03, Sri Lanka</p>
            <p>Hotline: +94 11 234 5678 | Email: info@sunrisedental.lk</p>
            <p>Registration No: PHSRC/COL/2026/894</p>
        </div>
        <div class="receipt-invoice-meta">
            <div class="invoice-title">OFFICIAL RECEIPT</div>
            <div style="font-weight: 700; color: var(--primary-dark); font-size: 1.1rem; margin-top: 0.25rem;"><%= r.getBillNumber() %></div>
            <div style="font-size: 0.85rem; color: var(--neutral-500); margin-top: 0.25rem;">Date: <%= r.getBillingDate() != null ? r.getBillingDate().toString().substring(0, 19) : "N/A" %></div>
            <div style="margin-top: 0.4rem;"><span class="badge badge-paid"><%= r.getPaymentStatus() %> - <%= r.getPaymentMethod() %></span></div>
        </div>
    </div>

    <!-- Patient & Consultation Info Grid -->
    <div class="receipt-patient-grid">
        <div>
            <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--neutral-500); font-weight: 700; margin-bottom: 0.3rem;">Billed To:</div>
            <div style="font-size: 1.1rem; font-weight: 700; color: var(--neutral-900);"><%= r.getPatientName() %></div>
            <div style="font-size: 0.85rem; color: var(--neutral-600);"><%= r.getPatientAddress() %></div>
            <div style="font-size: 0.85rem; color: var(--neutral-600);">Phone: <%= r.getPatientContact() %> | Code: <%= r.getPatientCode() %></div>
        </div>

        <div>
            <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--neutral-500); font-weight: 700; margin-bottom: 0.3rem;">Appointment Info:</div>
            <div><strong>Appt Ref:</strong> <%= r.getAppointmentNumber() %></div>
            <div><strong>Attending Doctor:</strong> <%= r.getDentistName() %> (<%= r.getDentistSpecialization() %>)</div>
            <div><strong>Facility:</strong> <%= r.getRoomNumber() %></div>
            <div><strong>Date & Time:</strong> <%= r.getAppointmentDate() %> at <%= r.getAppointmentTime() %></div>
        </div>
    </div>

    <!-- Itemized Breakdown Table -->
    <table class="custom-table" style="margin-bottom: 1.5rem;">
        <thead>
            <tr>
                <th>Service Description</th>
                <th style="text-align: right;">Standard Rate (Rs.)</th>
                <th style="text-align: right;">Amount (Rs.)</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>
                    <strong><%= r.getTreatmentName() %></strong>
                    <div style="font-size: 0.8rem; color: var(--neutral-500);">Dental Clinical Procedure</div>
                </td>
                <td style="text-align: right;"><%= String.format("%,.2f", r.getTreatmentCost()) %></td>
                <td style="text-align: right;"><%= String.format("%,.2f", r.getTreatmentCost()) %></td>
            </tr>
            <tr>
                <td>
                    <strong>Specialist Dental Consultation Fee</strong>
                    <div style="font-size: 0.8rem; color: var(--neutral-500);"><%= r.getDentistName() %></div>
                </td>
                <td style="text-align: right;"><%= String.format("%,.2f", r.getConsultationFee()) %></td>
                <td style="text-align: right;"><%= String.format("%,.2f", r.getConsultationFee()) %></td>
            </tr>
            <% if (r.getAdditionalCharges() != null && r.getAdditionalCharges().doubleValue() > 0) { %>
            <tr>
                <td>
                    <strong>Consumables / Lab Materials Surcharge</strong>
                </td>
                <td style="text-align: right;"><%= String.format("%,.2f", r.getAdditionalCharges()) %></td>
                <td style="text-align: right;"><%= String.format("%,.2f", r.getAdditionalCharges()) %></td>
            </tr>
            <% } %>
        </tbody>
    </table>

    <!-- Totals Summary -->
    <div class="receipt-totals">
        <div class="receipt-total-row">
            <span>Gross Subtotal:</span>
            <span>Rs. <%= String.format("%,.2f", r.getTreatmentCost().add(r.getConsultationFee()).add(r.getAdditionalCharges())) %></span>
        </div>
        <div class="receipt-total-row" style="color: var(--success); font-weight: 600;">
            <span>Tariff Discount / Concession (<%= r.getDiscountStrategy() %>):</span>
            <span>- Rs. <%= String.format("%,.2f", r.getDiscountAmount()) %></span>
        </div>
        <div class="receipt-total-row grand-total">
            <span>Net Amount Paid:</span>
            <span>Rs. <%= String.format("%,.2f", r.getTotalAmount()) %></span>
        </div>
    </div>

    <!-- Sign-off & Stamp -->
    <div style="margin-top: 3.5rem; display: flex; justify-content: space-between; align-items: flex-end; padding-top: 1.5rem; border-top: 1px dashed var(--neutral-300);">
        <div style="font-size: 0.8rem; color: var(--neutral-500); line-height: 1.4;">
            <div>Thank you for choosing Sunrise Dental Clinic.</div>
            <div>For aftercare emergencies, contact our 24/7 hotline.</div>
            <div style="font-style: italic; margin-top: 0.3rem;">This is a computer-generated official receipt.</div>
        </div>

        <div style="text-align: center; width: 220px;">
            <div style="border-bottom: 1px solid var(--neutral-800); height: 40px; margin-bottom: 0.3rem;"></div>
            <div style="font-size: 0.85rem; font-weight: 600; color: var(--neutral-700);">Authorized Staff Signature</div>
            <div style="font-size: 0.75rem; color: var(--neutral-500);">Sunrise Dental Clinic Colombo</div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
