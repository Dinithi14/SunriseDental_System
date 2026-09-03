<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.service.AppointmentService" %>
<%@ page import="com.sunrisedental.service.ReportService" %>
<%@ page import="com.sunrisedental.service.DentistService" %>
<%@ page import="com.sunrisedental.dto.ReportSummaryDTO" %>
<%@ page import="com.sunrisedental.dto.AppointmentDetailDTO" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%@ page import="java.util.List" %>

<jsp:include page="includes/header.jsp" />

<%
    ReportService reportService = new ReportService();
    AppointmentService appointmentService = new AppointmentService();
    DentistService dentistService = new DentistService();

    ReportSummaryDTO summary = reportService.getManagementSummary();
    List<AppointmentDetailDTO> recentAppointments = appointmentService.getAllAppointments();
    List<Dentist> dentists = dentistService.getActiveDentists();
%>

<div class="page-header">
    <div>
        <h1 class="page-title">Clinic Control Center</h1>
        <p class="page-subtitle">Welcome back, <strong><%= session.getAttribute("fullName") %></strong>. Here is the operational summary for today.</p>
    </div>
    <div style="display: flex; gap: 0.75rem;">
        <a href="${pageContext.request.contextPath}/appointments" class="btn btn-primary">➕ Book Appointment</a>
        <a href="${pageContext.request.contextPath}/patients" class="btn btn-secondary">👥 Register Patient</a>
    </div>
</div>

<!-- KPI Cards -->
<div class="grid-4" style="margin-bottom: 2rem;">
    <div class="stat-card">
        <div class="stat-icon blue">📅</div>
        <div>
            <div class="stat-value"><%= summary.getTotalAppointments() %></div>
            <div class="stat-label">Total Appointments</div>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-icon teal">🕒</div>
        <div>
            <div class="stat-value"><%= summary.getScheduledAppointments() %></div>
            <div class="stat-label">Pending / Scheduled</div>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-icon amber">👥</div>
        <div>
            <div class="stat-value"><%= summary.getTotalPatients() %></div>
            <div class="stat-label">Registered Patients</div>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-icon green">💰</div>
        <div>
            <div class="stat-value">Rs. <%= summary.getTotalRevenue() != null ? String.format("%,.2f", summary.getTotalRevenue()) : "0.00" %></div>
            <div class="stat-label">Total Clinic Revenue</div>
        </div>
    </div>
</div>

<!-- Main Split Section -->
<div class="grid-2" style="grid-template-columns: 2fr 1fr; gap: 1.5rem;">
    <!-- Recent Appointments Table -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Recent Patient Appointments</h2>
            <a href="${pageContext.request.contextPath}/appointments" class="btn btn-secondary btn-sm">View All</a>
        </div>

        <div class="table-responsive">
            <table class="custom-table">
                <thead>
                    <tr>
                        <th>Appt No</th>
                        <th>Patient</th>
                        <th>Dentist</th>
                        <th>Date & Time</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (recentAppointments == null || recentAppointments.isEmpty()) { %>
                        <tr><td colspan="6" style="text-align: center; color: #64748b; padding: 2rem;">No appointments found.</td></tr>
                    <% } else {
                        int count = 0;
                        for (AppointmentDetailDTO a : recentAppointments) {
                            if (count++ >= 5) break;
                            String bClass = a.getStatus() != null ? a.getStatus().toLowerCase() : "scheduled";
                    %>
                        <tr>
                            <td><strong><%= a.getAppointmentNumber() %></strong></td>
                            <td>
                                <div style="font-weight: 600;"><%= a.getPatientName() %></div>
                                <small style="color: #64748b;"><%= a.getPatientContact() %></small>
                            </td>
                            <td><%= a.getDentistName() %></td>
                            <td>
                                <div><%= a.getAppointmentDate() %></div>
                                <small style="color: #0284c7; font-weight: 600;"><%= a.getAppointmentTime() %></small>
                            </td>
                            <td><span class="badge badge-<%= bClass %>"><%= a.getStatus() %></span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/billing?appNo=<%= a.getAppointmentNumber() %>" class="btn btn-secondary btn-sm">Invoice</a>
                            </td>
                        </tr>
                    <%  }
                    } %>
                </tbody>
            </table>
        </div>
    </div>

    <!-- On-Duty Doctors & Quick Actions -->
    <div style="display: flex; flex-direction: column; gap: 1.5rem;">
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Attending Dental Surgeons</h2>
            </div>
            <div style="display: flex; flex-direction: column; gap: 0.9rem;">
                <% for (Dentist d : dentists) { %>
                    <div style="display: flex; align-items: center; justify-content: space-between; padding: 0.6rem 0; border-bottom: 1px solid var(--neutral-100);">
                        <div>
                            <div style="font-weight: 600; color: var(--neutral-900);"><%= d.getFullName() %></div>
                            <div style="font-size: 0.8rem; color: var(--neutral-500);"><%= d.getSpecialization() %> • <%= d.getRoomNumber() %></div>
                        </div>
                        <span class="badge badge-completed">Available</span>
                    </div>
                <% } %>
            </div>
        </div>

        <div class="card" style="background: linear-gradient(135deg, #0284c7, #075985); color: white;">
            <h2 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 0.5rem;">Automated Alerts Active</h2>
            <p style="font-size: 0.88rem; opacity: 0.9; margin-bottom: 1rem;">
                SMS & Email notification simulation is active. Double bookings are automatically prevented across all dentist schedules.
            </p>
            <a href="${pageContext.request.contextPath}/help.jsp" class="btn" style="background: white; color: #0284c7; font-weight: 600;">Explore Help Manual 📖</a>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
