<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.sunrisedental.dto.AppointmentDetailDTO" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%@ page import="com.sunrisedental.model.Treatment" %>
<%@ page import="com.sunrisedental.model.Patient" %>
<%@ page import="java.util.List" %>

<jsp:include page="includes/header.jsp" />

<%
    String ctx = request.getContextPath();
    List<AppointmentDetailDTO> appointments = (List<AppointmentDetailDTO>) request.getAttribute("appointments");
    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
    List<Treatment> treatments = (List<Treatment>) request.getAttribute("treatments");
    List<Patient> patients = (List<Patient>) request.getAttribute("patients");
%>

<div class="page-header">
    <div>
        <h1 class="page-title">Appointment Management</h1>
        <p class="page-subtitle">Schedule new consultations, manage dental sessions, and prevent slot overlaps.</p>
    </div>
    <button class="btn btn-primary" data-modal-target="bookAppointmentModal">
        ➕ Book New Appointment
    </button>
</div>

<% if (request.getAttribute("errorMessage") != null) { %>
    <div class="alert alert-danger">
        ⚠️ <%= request.getAttribute("errorMessage") %>
    </div>
<% } %>

<% if ("booked".equals(request.getParameter("success"))) { %>
    <div class="alert alert-success">
        ✅ Appointment <strong><%= request.getParameter("appNo") %></strong> booked successfully! Confirmation SMS & Email dispatched.
    </div>
<% } %>

<!-- Appointment Table Card -->
<div class="card">
    <div class="card-header">
        <h2 class="card-title">All Clinic Appointments</h2>
        <div style="font-size: 0.9rem; color: var(--neutral-500);">
            Total: <strong><%= appointments != null ? appointments.size() : 0 %></strong> bookings
        </div>
    </div>

    <div class="table-responsive">
        <table class="custom-table">
            <thead>
                <tr>
                    <th>Appt Reference</th>
                    <th>Patient Information</th>
                    <th>Dentist & Room</th>
                    <th>Treatment Service</th>
                    <th>Schedule</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% if (appointments == null || appointments.isEmpty()) { %>
                    <tr><td colspan="7" style="text-align: center; color: var(--neutral-500); padding: 2rem;">No appointments found.</td></tr>
                <% } else {
                    for (AppointmentDetailDTO a : appointments) {
                        String bClass = a.getStatus() != null ? a.getStatus().toLowerCase() : "scheduled";
                %>
                    <tr>
                        <td>
                            <strong style="color: var(--primary-dark);"><%= a.getAppointmentNumber() %></strong>
                        </td>
                        <td>
                            <div style="font-weight: 600;"><%= a.getPatientName() %></div>
                            <small style="color: var(--neutral-500);"><%= a.getPatientContact() %> • <%= a.getPatientCode() %></small>
                        </td>
                        <td>
                            <div style="font-weight: 500;"><%= a.getDentistName() %></div>
                            <small style="color: var(--neutral-500);"><%= a.getRoomNumber() %></small>
                        </td>
                        <td>
                            <div><%= a.getTreatmentName() %></div>
                            <small style="color: var(--neutral-500);">Standard: Rs. <%= String.format("%,.2f", a.getTreatmentCost()) %></small>
                        </td>
                        <td>
                            <div><%= a.getAppointmentDate() %></div>
                            <small style="color: var(--primary); font-weight: 600;"><%= a.getAppointmentTime() %></small>
                        </td>
                        <td>
                            <span class="badge badge-<%= bClass %>"><%= a.getStatus() %></span>
                        </td>
                        <td>
                            <div style="display: flex; gap: 0.4rem; align-items: center;">
                                <% if (!"COMPLETED".equalsIgnoreCase(a.getStatus())) { %>
                                    <a href="<%= ctx %>/billing?appNo=<%= a.getAppointmentNumber() %>" class="btn btn-primary btn-sm">
                                        💳 Bill
                                    </a>
                                <% } else { %>
                                    <a href="<%= ctx %>/billing?action=receipt&appointmentId=<%= a.getAppointmentId() %>" class="btn btn-success btn-sm">
                                        🧾 Receipt
                                    </a>
                                <% } %>

                                <!-- Status update form dropdown -->
                                <form action="<%= ctx %>/appointments" method="POST" style="display: inline;">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="appointmentId" value="<%= a.getAppointmentId() %>">
                                    <select name="status" onchange="this.form.submit()" class="form-select" style="padding: 0.25rem 0.5rem; font-size: 0.8rem; width: auto;">
                                        <option value="SCHEDULED" <%= "SCHEDULED".equals(a.getStatus()) ? "selected" : "" %>>Scheduled</option>
                                        <option value="IN_PROGRESS" <%= "IN_PROGRESS".equals(a.getStatus()) ? "selected" : "" %>>In Progress</option>
                                        <option value="COMPLETED" <%= "COMPLETED".equals(a.getStatus()) ? "selected" : "" %>>Completed</option>
                                        <option value="CANCELLED" <%= "CANCELLED".equals(a.getStatus()) ? "selected" : "" %>>Cancelled</option>
                                    </select>
                                </form>
                            </div>
                        </td>
                    </tr>
                <%  }
                } %>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal: Book New Appointment -->
<div id="bookAppointmentModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 style="font-size: 1.25rem; font-weight: 700;">Book New Dental Appointment</h3>
            <button class="close-btn modal-close">&times;</button>
        </div>

        <form action="<%= ctx %>/appointments" method="POST">
            <input type="hidden" name="action" value="book">

            <div class="modal-body">
                <!-- Patient Selector -->
                <div class="form-group">
                    <label class="form-label" for="patientSelect">Select Existing Patient or Register New *</label>
                    <select id="patientSelect" name="patientId" class="form-select" required>
                        <option value="">-- Choose Patient --</option>
                        <option value="NEW" style="font-weight: 700; color: var(--primary);">➕ Register New Patient Inline</option>
                        <% if (patients != null) {
                            for (Patient p : patients) { %>
                                <option value="<%= p.getId() %>"><%= p.getFullName() %> (<%= p.getContactNumber() %> - <%= p.getPatientCode() %>)</option>
                        <%  }
                        } %>
                    </select>
                </div>

                <!-- Inline New Patient Fields -->
                <div id="newPatientFields" style="display: none; background: var(--neutral-50); padding: 1rem; border-radius: var(--radius-md); margin-bottom: 1.25rem; border: 1px dashed var(--neutral-300);">
                    <h4 style="font-size: 0.95rem; margin-bottom: 0.75rem; color: var(--primary-dark);">New Patient Details</h4>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">Full Name *</label>
                            <input type="text" name="newPatientName" class="form-control" placeholder="e.g. Ruwan Silva">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Contact Number (10 digits) *</label>
                            <input type="text" name="newPatientContact" class="form-control" placeholder="e.g. 0771234567">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">Email</label>
                            <input type="email" name="newPatientEmail" class="form-control" placeholder="patient@gmail.com">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Gender</label>
                            <select name="newPatientGender" class="form-select">
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Address *</label>
                        <input type="text" name="newPatientAddress" class="form-control" placeholder="e.g. No 15, Galle Road, Colombo 03">
                    </div>

                    <div class="form-group" style="margin-bottom: 0;">
                        <label class="form-label">Medical History / Allergies</label>
                        <input type="text" name="newPatientMedicalHistory" class="form-control" placeholder="e.g. Penicillin allergy, Diabetes">
                    </div>
                </div>

                <!-- Dentist Selection -->
                <div class="form-group">
                    <label class="form-label" for="dentistId">Attending Dentist *</label>
                    <select id="dentistId" name="dentistId" class="form-select" required>
                        <option value="">-- Choose Dentist --</option>
                        <% if (dentists != null) {
                            for (Dentist d : dentists) { %>
                                <option value="<%= d.getId() %>"><%= d.getFullName() %> (<%= d.getSpecialization() %>) - Fee: Rs. <%= String.format("%,.2f", d.getConsultationFee()) %></option>
                        <%  }
                        } %>
                    </select>
                </div>

                <!-- Treatment Selection -->
                <div class="form-group">
                    <label class="form-label" for="treatmentId">Treatment Service *</label>
                    <select id="treatmentId" name="treatmentId" class="form-select" required>
                        <option value="">-- Choose Treatment --</option>
                        <% if (treatments != null) {
                            for (Treatment t : treatments) { %>
                                <option value="<%= t.getId() %>"><%= t.getTreatmentName() %> (Rs. <%= String.format("%,.2f", t.getStandardCost()) %> - <%= t.getEstimatedMinutes() %> mins)</option>
                        <%  }
                        } %>
                    </select>
                </div>

                <!-- Date & Time Slot -->
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="appointmentDate">Appointment Date *</label>
                        <input type="date" id="appointmentDate" name="appointmentDate" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="appointmentTime">Appointment Time Slot *</label>
                        <select id="appointmentTime" name="appointmentTime" class="form-select" required>
                            <option value="">-- Select Time Slot --</option>
                            <option value="09:00:00">09:00 AM</option>
                            <option value="09:30:00">09:30 AM</option>
                            <option value="10:00:00">10:00 AM</option>
                            <option value="10:30:00">10:30 AM</option>
                            <option value="11:00:00">11:00 AM</option>
                            <option value="11:30:00">11:30 AM</option>
                            <option value="14:00:00">02:00 PM</option>
                            <option value="14:30:00">02:30 PM</option>
                            <option value="15:00:00">03:00 PM</option>
                            <option value="15:30:00">03:30 PM</option>
                            <option value="16:00:00">04:00 PM</option>
                            <option value="16:30:00">04:30 PM</option>
                            <option value="17:00:00">05:00 PM</option>
                            <option value="17:30:00">05:30 PM</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="notes">Clinical Notes / Symptoms</label>
                    <textarea id="notes" name="notes" rows="2" class="form-control" placeholder="Describe symptoms or reasons for visit..."></textarea>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary modal-close">Cancel</button>
                <button type="submit" class="btn btn-primary">Confirm & Book Appointment</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
