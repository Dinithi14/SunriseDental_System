<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.sunrisedental.dto.ReportSummaryDTO" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>

<jsp:include page="includes/header.jsp" />

<%
    String ctx = request.getContextPath();
    ReportSummaryDTO r = (ReportSummaryDTO) request.getAttribute("report");
    if (r == null) {
        response.sendRedirect(ctx + "/reports");
        return;
    }
%>

<div class="page-header">
    <div>
        <h1 class="page-title">Executive Analytics & Clinical Reports</h1>
        <p class="page-subtitle">Data-driven insights to facilitate clinical resource allocation, dentist scheduling, and revenue governance.</p>
    </div>
    <button onclick="window.print()" class="btn btn-secondary no-print">
        🖨️ Export / Print Report
    </button>
</div>

<!-- High-Level Financial & Operational KPIs -->
<div class="grid-4" style="margin-bottom: 2rem;">
    <div class="stat-card">
        <div class="stat-icon green">💰</div>
        <div>
            <div class="stat-value">Rs. <%= String.format("%,.2f", r.getTotalRevenue()) %></div>
            <div class="stat-label">Total Revenue Realized</div>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-icon blue">📅</div>
        <div>
            <div class="stat-value"><%= r.getTotalAppointments() %></div>
            <div class="stat-label">Total Appointments Handled</div>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-icon teal">✅</div>
        <div>
            <div class="stat-value"><%= r.getCompletedAppointments() %></div>
            <div class="stat-label">Completed Consultations</div>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-icon amber">👥</div>
        <div>
            <div class="stat-value"><%= r.getTotalPatients() %></div>
            <div class="stat-label">Registered Patient Base</div>
        </div>
    </div>
</div>

<div class="grid-2" style="margin-bottom: 2rem;">
    <!-- Report 1: Dentist Workload & Patient Distribution -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Dentist Patient Workload & Utilization</h2>
        </div>

        <div class="table-responsive">
            <table class="custom-table">
                <thead>
                    <tr>
                        <th>Dentist Name</th>
                        <th>Specialization</th>
                        <th style="text-align: right;">Total Consultations</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (r.getAppointmentsByDentist() != null) {
                        for (Map<String, Object> item : r.getAppointmentsByDentist()) { %>
                        <tr>
                            <td><strong><%= item.get("dentistName") %></strong></td>
                            <td style="color: var(--neutral-600);"><%= item.get("specialization") %></td>
                            <td style="text-align: right; font-weight: 700; color: var(--primary);">
                                <%= item.get("appointmentCount") %>
                            </td>
                        </tr>
                    <%  }
                    } %>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Report 2: Treatment Revenue Contribution -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Treatment Demand & Procedure Popularity</h2>
        </div>

        <div class="table-responsive">
            <table class="custom-table">
                <thead>
                    <tr>
                        <th>Dental Procedure</th>
                        <th style="text-align: center;">Sessions</th>
                        <th style="text-align: right;">Unit Value</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (r.getAppointmentsByTreatment() != null) {
                        for (Map<String, Object> item : r.getAppointmentsByTreatment()) { %>
                        <tr>
                            <td><strong><%= item.get("treatmentName") %></strong></td>
                            <td style="text-align: center;"><span class="badge badge-scheduled"><%= item.get("count") %></span></td>
                            <td style="text-align: right; font-weight: 600;">
                                Rs. <%= item.get("totalValue") != null ? String.format("%,.2f", (BigDecimal) item.get("totalValue")) : "0.00" %>
                            </td>
                        </tr>
                    <%  }
                    } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Report 3: Daily Revenue Audit Log -->
<div class="card">
    <div class="card-header">
        <h2 class="card-title">Daily Financial Settlement Log</h2>
    </div>

    <div class="table-responsive">
        <table class="custom-table">
            <thead>
                <tr>
                    <th>Settlement Date</th>
                    <th>Invoices Settled</th>
                    <th style="text-align: right;">Total Income Collected</th>
                </tr>
            </thead>
            <tbody>
                <% if (r.getRevenueByDay() != null && !r.getRevenueByDay().isEmpty()) {
                    for (Map<String, Object> item : r.getRevenueByDay()) { %>
                    <tr>
                        <td><strong><%= item.get("date") %></strong></td>
                        <td><%= item.get("count") %> Invoices</td>
                        <td style="text-align: right; font-weight: 700; color: var(--success);">
                            Rs. <%= item.get("revenue") != null ? String.format("%,.2f", (BigDecimal) item.get("revenue")) : "0.00" %>
                        </td>
                    </tr>
                <%  }
                } else { %>
                    <tr><td colspan="3" style="text-align: center; color: var(--neutral-500); padding: 2rem;">No settled revenue entries logged yet.</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
