<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.dto.AppointmentDetailDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>

<jsp:include page="includes/header.jsp" />

<%
    List<AppointmentDetailDTO> appointments = (List<AppointmentDetailDTO>) request.getAttribute("appointments");
    AppointmentDetailDTO selected = (AppointmentDetailDTO) request.getAttribute("selectedAppointment");
%>

<div class="page-header">
    <div>
        <h1 class="page-title">Billing & Fee Calculation Engine</h1>
        <p class="page-subtitle">Compute treatment fee, doctor consultation charges, discounts (Strategy Pattern), and produce invoices.</p>
    </div>
</div>

<% if (request.getAttribute("errorMessage") != null) { %>
    <div class="alert alert-danger">
        ⚠️ <%= request.getAttribute("errorMessage") %>
    </div>
<% } %>

<div class="grid-2" style="grid-template-columns: 1.2fr 1fr; gap: 2rem;">
    <!-- Bill Generation Form -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Calculate Patient Invoice</h2>
        </div>

        <form action="${pageContext.request.contextPath}/billing" method="POST">
            <input type="hidden" name="action" value="generate">

            <!-- Appointment Selector -->
            <div class="form-group">
                <label class="form-label" for="billingApptSelect">Select Appointment to Bill *</label>
                <select id="billingApptSelect" name="appointmentId" class="form-select" required>
                    <option value="">-- Choose Appointment --</option>
                    <% if (appointments != null) {
                        for (AppointmentDetailDTO a : appointments) {
                            boolean isSel = (selected != null && selected.getAppointmentId() == a.getAppointmentId())
                                    || (a.getAppointmentNumber().equals(request.getParameter("appNo")));
                    %>
                        <option value="<%= a.getAppointmentId() %>"
                                data-treatment-cost="<%= a.getTreatmentCost() %>"
                                data-consultation-fee="<%= a.getConsultationFee() %>"
                                <%= isSel ? "selected" : "" %>>
                            <%= a.getAppointmentNumber() %> - <%= a.getPatientName() %> (<%= a.getTreatmentName() %> - <%= a.getDentistName() %>)
                        </option>
                    <%  }
                    } %>
                </select>
            </div>

            <!-- Discount Scheme / Strategy Selector (Strategy Pattern) -->
            <div class="form-group">
                <label class="form-label" for="billingStrategySelect">Pricing Strategy / Discount Scheme *</label>
                <select id="billingStrategySelect" name="strategy" class="form-select" required>
                    <option value="STANDARD">Standard Clinic Tariff (0% Discount)</option>
                    <option value="SENIOR_DISCOUNT">Senior Citizen Scheme (10% Overall Discount)</option>
                    <option value="CHILD_DISCOUNT">Pediatric / Child Discount (15% Concession)</option>
                    <option value="INSURANCE">Dental Insurance Coverage (80% Insurer Covered / 20% Patient Co-Pay)</option>
                    <option value="EMERGENCY">Emergency Priority Care (+20% Urgent Surcharge)</option>
                </select>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label" for="additionalCharges">Additional Charges (Consumables / Lab)</label>
                    <input type="number" step="0.01" id="additionalCharges" name="additionalCharges" class="form-control" value="0.00">
                </div>

                <div class="form-group">
                    <label class="form-label" for="paymentMethod">Payment Method *</label>
                    <select id="paymentMethod" name="paymentMethod" class="form-select" required>
                        <option value="CASH">Cash Settlement</option>
                        <option value="CARD">Credit / Debit Card</option>
                        <option value="INSURANCE">Direct Insurance Claim</option>
                        <option value="ONLINE">Online Bank Transfer</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label" for="notes">Invoice Remarks</label>
                <input type="text" id="notes" name="notes" class="form-control" placeholder="Optional billing remarks (e.g. Cleared at Reception Counter)">
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.85rem; font-size: 1rem; margin-top: 0.5rem;">
                🧾 Generate & Save Official Bill
            </button>
        </form>
    </div>

    <!-- Live Real-Time Bill Breakdown Card -->
    <div class="card" style="background: var(--neutral-50); border: 2px solid var(--primary-light);">
        <div class="card-header">
            <h2 class="card-title">Live Computation Summary</h2>
            <span class="badge badge-paid">Real-Time</span>
        </div>

        <div style="display: flex; flex-direction: column; gap: 1rem; padding: 0.5rem 0;">
            <div style="display: flex; justify-content: space-between; color: var(--neutral-700);">
                <span>Treatment Standard Fee:</span>
                <strong id="previewTreatmentCost">Rs. 0.00</strong>
            </div>

            <div style="display: flex; justify-content: space-between; color: var(--neutral-700);">
                <span>Doctor Consultation Fee:</span>
                <strong id="previewConsultationFee">Rs. 0.00</strong>
            </div>

            <div style="display: flex; justify-content: space-between; color: var(--neutral-700);">
                <span>Additional Lab / Consumable Charges:</span>
                <strong id="previewAdditionalCharges">Rs. 0.00</strong>
            </div>

            <div style="display: flex; justify-content: space-between; color: var(--primary-dark); font-weight: 600;">
                <span>Discount / Adjustment:</span>
                <strong id="previewDiscount" style="color: var(--success);">- Rs. 0.00</strong>
            </div>

            <hr style="border: 0; border-top: 1px dashed var(--neutral-300);">

            <div style="display: flex; justify-content: space-between; align-items: center;">
                <span style="font-size: 1.1rem; font-weight: 700; color: var(--neutral-900);">Net Payable Amount:</span>
                <span id="previewTotal" style="font-size: 1.5rem; font-weight: 800; color: var(--primary-dark);">Rs. 0.00</span>
            </div>
        </div>

        <div style="margin-top: 1.5rem; background: white; padding: 1rem; border-radius: var(--radius-md); font-size: 0.85rem; color: var(--neutral-600); border: 1px solid var(--border-color);">
            💡 <strong>Design Pattern Note:</strong> The pricing adjustments and concessions are evaluated dynamically via the <code>BillingStrategy</code> Strategy Pattern and Factory.
        </div>
    </div>
</div>

<script>
    // Trigger initial preview computation on load
    document.addEventListener('DOMContentLoaded', () => {
        if (typeof updateBillingPreview === 'function') {
            updateBillingPreview();
        }
    });
</script>

<jsp:include page="includes/footer.jsp" />
