<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.sunrisedental.model.Patient" %>
<%@ page import="java.util.List" %>

<jsp:include page="includes/header.jsp" />

<%
    String ctx = request.getContextPath();
    List<Patient> patients = (List<Patient>) request.getAttribute("patients");
%>

<div class="page-header">
    <div>
        <h1 class="page-title">Patient Directory & Registration</h1>
        <p class="page-subtitle">Maintain patient profiles, contact records, and dental medical history.</p>
    </div>
    <button class="btn btn-primary" data-modal-target="registerPatientModal">
        ➕ Register New Patient
    </button>
</div>

<% if (request.getAttribute("errorMessage") != null) { %>
    <div class="alert alert-danger">
        ⚠️ <%= request.getAttribute("errorMessage") %>
    </div>
<% } %>

<% if ("created".equals(request.getParameter("success"))) { %>
    <div class="alert alert-success">
        ✅ Patient profile registered successfully!
    </div>
<% } %>

<!-- Patients Table Card -->
<div class="card">
    <div class="card-header">
        <h2 class="card-title">Registered Patients Database</h2>
        <div style="font-size: 0.9rem; color: var(--neutral-500);">
            Total Patients: <strong><%= patients != null ? patients.size() : 0 %></strong>
        </div>
    </div>

    <div class="table-responsive">
        <table class="custom-table">
            <thead>
                <tr>
                    <th>Patient Code</th>
                    <th>Full Name</th>
                    <th>NIC / Passport</th>
                    <th>Contact Phone</th>
                    <th>Address</th>
                    <th>Gender & Blood Group</th>
                    <th>Medical History / Notes</th>
                </tr>
            </thead>
            <tbody>
                <% if (patients == null || patients.isEmpty()) { %>
                    <tr><td colspan="7" style="text-align: center; color: var(--neutral-500); padding: 2rem;">No patients registered yet.</td></tr>
                <% } else {
                    for (Patient p : patients) { %>
                    <tr>
                        <td><strong style="color: var(--primary);"><%= p.getPatientCode() %></strong></td>
                        <td>
                            <div style="font-weight: 600;"><%= p.getFullName() %></div>
                            <small style="color: var(--neutral-500);"><%= p.getEmail() != null ? p.getEmail() : "No email" %></small>
                        </td>
                        <td><%= p.getNicPassport() != null ? p.getNicPassport() : "-" %></td>
                        <td><strong><%= p.getContactNumber() %></strong></td>
                        <td style="max-width: 250px; font-size: 0.85rem;"><%= p.getAddress() %></td>
                        <td>
                            <span class="badge badge-scheduled"><%= p.getGender() %></span>
                            <span class="badge" style="background: var(--neutral-100);"><%= p.getBloodGroup() %></span>
                        </td>
                        <td style="font-size: 0.85rem; color: var(--neutral-600);"><%= p.getMedicalHistory() != null ? p.getMedicalHistory() : "None" %></td>
                    </tr>
                <%  }
                } %>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal: Register Patient -->
<div id="registerPatientModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 style="font-size: 1.25rem; font-weight: 700;">Register New Patient</h3>
            <button class="close-btn modal-close">&times;</button>
        </div>

        <form action="<%= ctx %>/patients" method="POST">
            <input type="hidden" name="action" value="register">

            <div class="modal-body">
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Full Name *</label>
                        <input type="text" name="fullName" class="form-control" placeholder="e.g. Kasun Mendis" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">NIC / Passport Number</label>
                        <input type="text" name="nicPassport" class="form-control" placeholder="e.g. 199012345678">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Contact Number (10 digits) *</label>
                        <input type="text" name="contactNumber" class="form-control" placeholder="e.g. 0774441122" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Email Address</label>
                        <input type="email" name="email" class="form-control" placeholder="patient@gmail.com">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Residential Address *</label>
                    <textarea name="address" rows="2" class="form-control" placeholder="No. 45/2, Galle Road, Colombo 03" required></textarea>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Date of Birth</label>
                        <input type="date" name="dateOfBirth" class="form-control">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Gender</label>
                        <select name="gender" class="form-select">
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                            <option value="OTHER">Other</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Blood Group</label>
                        <select name="bloodGroup" class="form-select">
                            <option value="A+">A+</option>
                            <option value="A-">A-</option>
                            <option value="B+">B+</option>
                            <option value="B-">B-</option>
                            <option value="O+">O+</option>
                            <option value="O-">O-</option>
                            <option value="AB+">AB+</option>
                            <option value="AB-">AB-</option>
                            <option value="N/A">Unknown / N/A</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Emergency Contact Person & Phone</label>
                    <input type="text" name="emergencyContact" class="form-control" placeholder="e.g. Spouse / Parent (0774441100)">
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label class="form-label">Medical Background / Allergies</label>
                    <textarea name="medicalHistory" rows="2" class="form-control" placeholder="Specify any chronic health conditions, heart diseases, diabetes, or drug allergies..."></textarea>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary modal-close">Cancel</button>
                <button type="submit" class="btn btn-primary">Save Patient Profile</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
