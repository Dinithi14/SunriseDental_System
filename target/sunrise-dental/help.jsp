<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="includes/header.jsp" />

<div class="page-header">
    <div>
        <h1 class="page-title">Staff Onboarding & System Help Manual</h1>
        <p class="page-subtitle">Standard Operating Procedures (SOP) and step-by-step guidance for Sunrise Dental Clinic personnel.</p>
    </div>
</div>

<div class="grid-2" style="gap: 1.5rem; margin-bottom: 2rem;">
    <!-- Step 1: Authentication & Access Control -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">1. Staff Authentication & Access</h2>
            <span class="badge badge-scheduled">Step 1</span>
        </div>
        <p style="color: var(--neutral-600); font-size: 0.92rem; line-height: 1.6;">
            • Every staff member must log in using their authorized username and credentials.<br>
            • All passwords are encrypted with SHA-256 and salted for hospital data security.<br>
            • System sessions automatically expire after 30 minutes of inactivity to safeguard patient confidentiality.<br>
            • To safely close your session, always click <strong>🚪 Exit</strong> in the top-right corner.
        </p>
    </div>

    <!-- Step 2: Patient Registration -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">2. Patient Registration & Records</h2>
            <span class="badge badge-scheduled">Step 2</span>
        </div>
        <p style="color: var(--neutral-600); font-size: 0.92rem; line-height: 1.6;">
            • Navigate to <strong>👥 Patients</strong> tab to view existing patients or click <strong>➕ Register New Patient</strong>.<br>
            • Record Full Name, Contact Number (validated 10-digit Sri Lankan phone number), NIC, Address, Blood Group, and Allergies/Medical History.<br>
            • The system automatically assigns a unique patient identifier (e.g. <code>PAT-001</code>).
        </p>
    </div>

    <!-- Step 3: Booking Appointments & Conflict Prevention -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">3. Scheduling Appointments (No Double-Booking)</h2>
            <span class="badge badge-scheduled">Step 3</span>
        </div>
        <p style="color: var(--neutral-600); font-size: 0.92rem; line-height: 1.6;">
            • Navigate to <strong>📅 Appointments</strong> and click <strong>➕ Book New Appointment</strong>.<br>
            • Choose an existing patient or select <em>➕ Register New Patient Inline</em>.<br>
            • Select the attending dentist, treatment service, appointment date, and time slot.<br>
            • <strong>Automated Conflict Detection:</strong> The system automatically blocks any double bookings if the selected dentist already has an active appointment at that time slot.
        </p>
    </div>

    <!-- Step 4: Searching & Dossier Lookup -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">4. Search & View Appointment Details</h2>
            <span class="badge badge-scheduled">Step 4</span>
        </div>
        <p style="color: var(--neutral-600); font-size: 0.92rem; line-height: 1.6;">
            • Navigate to <strong>🔍 Search Details</strong>.<br>
            • Enter the unique Appointment Reference (e.g. <code>APT-2026-0001</code>), Patient Name, or Contact Phone Number.<br>
            • Click <strong>🔍 Full Details</strong> to inspect patient profile, treatment details, doctor room, and financial status.
        </p>
    </div>

    <!-- Step 5: Billing & Fee Calculation Strategy -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">5. Fee Calculation & Pricing Schemes</h2>
            <span class="badge badge-scheduled">Step 5</span>
        </div>
        <p style="color: var(--neutral-600); font-size: 0.92rem; line-height: 1.6;">
            • Go to <strong>💳 Billing & Invoice</strong> and select the patient's appointment.<br>
            • The base calculation automatically sums: <code>Treatment Fee + Doctor Consultation Fee + Additional Consumables</code>.<br>
            • Select the pricing tariff strategy:<br>
            &nbsp;&nbsp;&nbsp;&nbsp;🏷️ <em>Standard Tariff:</em> Normal rates.<br>
            &nbsp;&nbsp;&nbsp;&nbsp;👴 <em>Senior Citizen:</em> 10% concession.<br>
            &nbsp;&nbsp;&nbsp;&nbsp;👶 <em>Child Discount:</em> 15% pediatric reduction.<br>
            &nbsp;&nbsp;&nbsp;&nbsp;🏥 <em>Insurance Coverage:</em> 80% direct claim, 20% patient co-pay.<br>
            &nbsp;&nbsp;&nbsp;&nbsp;🚨 <em>Emergency Priority:</em> 20% emergency surcharge.
        </p>
    </div>

    <!-- Step 6: Printable Receipts & Invoices -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">6. Printing Patient Receipts & Invoices</h2>
            <span class="badge badge-scheduled">Step 6</span>
        </div>
        <p style="color: var(--neutral-600); font-size: 0.92rem; line-height: 1.6;">
            • Click <strong>🧾 Generate & Save Official Bill</strong>.<br>
            • The system automatically produces an itemized, formatted hospital invoice with clinic header, itemized breakdown, discounts, and signature lines.<br>
            • Click <strong>🖨️ Print Patient Bill / Receipt</strong> to send directly to the clinic POS/receipt printer.
        </p>
    </div>
</div>

<!-- Architecture & Design Patterns Card for Coursework Criteria -->
<div class="card" style="background: linear-gradient(135deg, #f8fafc, #e0f2fe); border: 2px solid var(--primary);">
    <div class="card-header">
        <h2 class="card-title" style="color: var(--primary-dark);">System Architecture & Design Patterns Reference</h2>
        <span class="badge badge-completed">CIS6003 Criteria</span>
    </div>
    <div style="font-size: 0.92rem; color: var(--neutral-700); line-height: 1.6;">
        <p><strong>1. Three-Tier Layered Architecture:</strong> Presentation Tier (HTML5/CSS3/JS/JSP) ➔ Business Logic Tier (POJO Services) ➔ Data Access Tier (DAO & JDBC) ➔ Database (MySQL XAMPP).</p>
        <p><strong>2. Design Patterns Implemented:</strong></p>
        <ul style="margin-left: 1.5rem; margin-top: 0.4rem;">
            <li><strong>Singleton Pattern:</strong> <code>DatabaseConnection</code> provides a single thread-safe connection manager.</li>
            <li><strong>DAO Pattern:</strong> <code>PatientDAO</code>, <code>AppointmentDAO</code>, <code>DentistDAO</code>, <code>TreatmentDAO</code>, <code>BillDAO</code>, <code>UserDAO</code>.</li>
            <li><strong>Factory Pattern:</strong> <code>DAOFactory</code> and <code>BillingStrategyFactory</code>.</li>
            <li><strong>Strategy Pattern:</strong> <code>BillingStrategy</code> and concrete tariff implementations.</li>
            <li><strong>Observer Pattern:</strong> <code>NotificationPublisher</code> simulating automated SMS/Email reminders on appointment events.</li>
            <li><strong>Model-View-Controller (MVC):</strong> Java Servlets controller dispatching to Model DTOs and JSP views.</li>
        </ul>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
