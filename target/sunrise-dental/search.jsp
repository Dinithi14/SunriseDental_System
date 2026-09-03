<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.sunrisedental.service.AppointmentService" %>
<%@ page import="com.sunrisedental.dto.AppointmentDetailDTO" %>
<%@ page import="java.util.List" %>

<jsp:include page="includes/header.jsp" />

<%
    String ctx = request.getContextPath();
    AppointmentService appointmentService = new AppointmentService();
    List<AppointmentDetailDTO> allAppointments = appointmentService.getAllAppointments();
%>

<div class="page-header">
    <div>
        <h1 class="page-title">Search & Display Appointment Details</h1>
        <p class="page-subtitle">Instant lookup by Appointment Number, Patient Name, NIC, or Phone Number.</p>
    </div>
</div>

<!-- Search Input Card -->
<div class="search-box-container">
    <div class="search-input-group">
        <div style="flex: 2; min-width: 280px;">
            <label class="form-label" for="searchQuery">Search Keyword</label>
            <input type="text" id="searchQuery" class="form-control" placeholder="Search by Appointment No (e.g. APT-2026-0001), Patient Name, Phone..." autofocus>
        </div>

        <div style="flex: 1; min-width: 180px;">
            <label class="form-label" for="statusFilter">Filter by Status</label>
            <select id="statusFilter" class="form-select">
                <option value="ALL">All Statuses</option>
                <option value="SCHEDULED">Scheduled</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
            </select>
        </div>

        <div style="align-self: flex-end;">
            <button id="btnSearch" class="btn btn-primary" style="padding: 0.7rem 1.5rem;">🔍 Search Records</button>
        </div>
    </div>
</div>

<!-- Results Table -->
<div class="card">
    <div class="card-header">
        <h2 class="card-title">Search Results</h2>
    </div>

    <div class="table-responsive">
        <table class="custom-table">
            <thead>
                <tr>
                    <th>Appt Reference</th>
                    <th>Patient Name & Contact</th>
                    <th>Dentist</th>
                    <th>Treatment</th>
                    <th>Schedule</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="searchResultsBody">
                <% if (allAppointments == null || allAppointments.isEmpty()) { %>
                    <tr><td colspan="7" style="text-align: center; color: var(--neutral-500); padding: 2rem;">No appointments found.</td></tr>
                <% } else {
                    for (AppointmentDetailDTO a : allAppointments) {
                        String bClass = a.getStatus() != null ? a.getStatus().toLowerCase() : "scheduled";
                %>
                    <tr>
                        <td><strong><%= a.getAppointmentNumber() %></strong></td>
                        <td>
                            <div style="font-weight: 600;"><%= a.getPatientName() %></div>
                            <small style="color: var(--neutral-500);"><%= a.getPatientContact() %> | <%= a.getPatientCode() %></small>
                        </td>
                        <td>
                            <div><%= a.getDentistName() %></div>
                            <small style="color: var(--neutral-500);"><%= a.getDentistSpecialization() %></small>
                        </td>
                        <td><%= a.getTreatmentName() %></td>
                        <td>
                            <div><%= a.getAppointmentDate() %></div>
                            <small style="color: var(--primary); font-weight: 600;"><%= a.getAppointmentTime() %></small>
                        </td>
                        <td><span class="badge badge-<%= bClass %>"><%= a.getStatus() %></span></td>
                        <td>
                            <div style="display: flex; gap: 0.5rem;">
                                <button class="btn btn-secondary btn-sm" onclick="viewAppointmentDetail('<%= a.getAppointmentNumber() %>')">
                                    🔍 Full Details
                                </button>
                                <% if (!"COMPLETED".equalsIgnoreCase(a.getStatus())) { %>
                                    <a href="<%= ctx %>/billing?appNo=<%= a.getAppointmentNumber() %>" class="btn btn-primary btn-sm">💳 Bill</a>
                                <% } else { %>
                                    <a href="<%= ctx %>/billing?action=receipt&appointmentId=<%= a.getAppointmentId() %>" class="btn btn-success btn-sm">🧾 Receipt</a>
                                <% } %>
                            </div>
                        </td>
                    </tr>
                <%  }
                } %>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal: Complete Appointment Details View (Meets Requirement 3) -->
<div id="appointmentDetailModal" class="modal">
    <div class="modal-content" style="max-width: 700px;">
        <div class="modal-header">
            <div>
                <h3 style="font-size: 1.3rem; font-weight: 700;">Appointment Dossier</h3>
                <span id="modalAppNo" style="font-size: 0.9rem; font-weight: 700; color: var(--primary);"></span>
            </div>
            <button class="close-btn modal-close">&times;</button>
        </div>

        <div class="modal-body">
            <!-- Patient Section -->
            <div style="background: var(--neutral-50); padding: 1.25rem; border-radius: var(--radius-md); margin-bottom: 1.25rem; border: 1px solid var(--border-color);">
                <h4 style="font-size: 0.95rem; color: var(--primary-dark); margin-bottom: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em;">Patient Profile</h4>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; font-size: 0.95rem;">
                    <div><strong style="color: var(--neutral-500);">Full Name:</strong> <span id="modalPatientName" style="font-weight: 600;"></span></div>
                    <div><strong style="color: var(--neutral-500);">Patient Code:</strong> <span id="modalPatientCode"></span></div>
                    <div><strong style="color: var(--neutral-500);">Contact Phone:</strong> <span id="modalPatientContact"></span></div>
                    <div><strong style="color: var(--neutral-500);">Home Address:</strong> <span id="modalPatientAddress"></span></div>
                </div>
            </div>

            <!-- Doctor & Treatment Section -->
            <div style="background: var(--neutral-50); padding: 1.25rem; border-radius: var(--radius-md); margin-bottom: 1.25rem; border: 1px solid var(--border-color);">
                <h4 style="font-size: 0.95rem; color: var(--secondary); margin-bottom: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em;">Clinical Details</h4>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; font-size: 0.95rem;">
                    <div><strong style="color: var(--neutral-500);">Attending Dentist:</strong> <span id="modalDentist" style="font-weight: 600;"></span></div>
                    <div><strong style="color: var(--neutral-500);">Consultation Room:</strong> <span id="modalRoom"></span></div>
                    <div><strong style="color: var(--neutral-500);">Treatment Procedure:</strong> <span id="modalTreatment"></span></div>
                    <div><strong style="color: var(--neutral-500);">Schedule:</strong> <span id="modalDate" style="color: var(--primary); font-weight: 600;"></span></div>
                </div>
            </div>

            <!-- Clinical Notes & Status -->
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label class="form-label">Current Status</label>
                    <div id="modalStatus" style="font-weight: 700; color: var(--primary-dark);"></div>
                </div>
                <div>
                    <label class="form-label">Clinical Notes</label>
                    <div id="modalNotes" style="font-size: 0.9rem; color: var(--neutral-600); background: white; padding: 0.5rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm);"></div>
                </div>
            </div>
        </div>

        <div class="modal-footer">
            <button type="button" class="btn btn-secondary modal-close">Close</button>
            <div id="modalActionButtons" style="display: inline-flex; gap: 0.5rem;"></div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
