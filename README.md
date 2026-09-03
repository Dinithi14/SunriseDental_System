# Sunrise Dental Clinic Management System (Colombo)
**Module:** CIS6003 Advanced Programming (WRIT1)  
**Architecture:** 3-Tier Enterprise Distributed Web Architecture (Pure Java EE, No Frameworks)  
**Database:** MySQL (XAMPP Compatible)  
**Build Tool:** Maven  
**Testing:** JUnit 5 Automated Test Suite (TDD Approach)  

---

## 🌟 Executive Summary & Features
This computerized system was developed to replace manual paper records at **Sunrise Dental Clinic (Colombo)**, successfully eliminating double bookings, lost records, long waiting times, and billing errors.

### Core Modules:
1. **User Authentication & Access Control:**
   - Role-based permissions (`ADMIN`, `RECEPTIONIST`, `DENTIST`).
   - Salted SHA-256 cryptographic password security.
   - 30-minute session expiration.
2. **Patient Registration & Records:**
   - Unique patient reference (`PAT-001`, `PAT-002`).
   - Contact numbers (validated 10-digit Sri Lankan format), emergency contacts, blood group, and medical history/allergies.
3. **Appointment Scheduling (No Double-Booking):**
   - Unique reference format (`APT-2026-XXXX`).
   - Real-time conflict detection preventing dentist overlap on identical date/time slots.
   - Observer Pattern notification triggers (SMS & Email alerts simulation).
4. **Search & Complete Dossier Lookup:**
   - Instant search by Appointment No, Patient Name, NIC, Contact Phone, or Attending Dentist.
   - Complete dossier modal with patient profile, treatment details, doctor suite, and billing status.
5. **Billing & Invoicing Engine (Strategy Pattern):**
   - Base cost: `Treatment Standard Fee + Doctor Consultation Fee + Consumables/Lab Surcharge`.
   - Dynamic Strategy Pattern pricing schemes:
     - 🏷️ **Standard Tariff:** 0% concession.
     - 👴 **Senior Citizen Scheme:** 10% overall discount.
     - 👶 **Pediatric / Child Discount:** 15% concession.
     - 🏥 **Dental Insurance Coverage:** 80% direct claim, 20% patient co-pay.
     - 🚨 **Emergency Priority Care:** 20% urgent surgical surcharge.
   - Official printable receipt & invoice with clinic header, itemized breakdown, and authorized signature.
6. **Executive Management Analytics:**
   - Total & daily revenue realization reports.
   - Dentist patient workload distribution.
   - Treatment procedure popularity and revenue contribution.
7. **Staff Onboarding & Help Manual:**
   - Step-by-step SOP guide covering daily operations and workflows.

---

## 📐 Design Patterns Implemented (LO II - 40 Marks)
1. **Singleton Pattern:** [`DatabaseConnection`](file:///c:/Users/dinit/OneDrive/Desktop/6003AP/src/main/java/com/sunrisedental/config/DatabaseConnection.java) provides a centralized, thread-safe access point for JDBC connection management.
2. **Data Access Object (DAO) Pattern:** `UserDAO`, `PatientDAO`, `DentistDAO`, `TreatmentDAO`, `AppointmentDAO`, `BillDAO` cleanly decouple business operations from database persistence.
3. **Factory Pattern:**
   - `DAOFactory` decouples service layer from concrete DAO implementations.
   - `BillingStrategyFactory` instantiates appropriate pricing algorithms dynamically.
4. **Strategy Pattern:** `BillingStrategy` hierarchy (`Standard`, `SeniorDiscount`, `ChildDiscount`, `Insurance`, `Emergency`) encapsulates discount algorithms for flexible runtime tariff calculation.
5. **Observer Pattern:** `NotificationPublisher` dispatches events to `SMSNotificationService` and `EmailNotificationService` on appointment confirmation and status transitions.
6. **Data Transfer Object (DTO) Pattern:** `AppointmentDetailDTO`, `BillReceiptDTO`, `ReportSummaryDTO` encapsulate multi-entity data transfers.
7. **Model-View-Controller (MVC):** Native Java Servlets act as controllers processing requests and forwarding to JSP/HTML views and model DTOs.

---

## 🗄️ Database Setup (XAMPP MySQL)
1. Open the **XAMPP Control Panel** and click **Start** next to **MySQL**.
2. Open your web browser and go to `http://localhost/phpmyadmin`.
3. Click on the **Import** tab or open the SQL console.
4. Import the file located at:
   ```
   database/schema.sql
   ```
5. The database `sunrise_dental_db` will be created with all tables, triggers, stored procedures, views, and initial sample data.

### Default Staff Login Credentials:
| Role | Username | Password | Access Level |
|---|---|---|---|
| **Admin** | `admin` | `admin123` | Full clinic management, analytics, and billing |
| **Receptionist** | `receptionist` | `recep123` | Patient registration, appointment booking, billing |
| **Dentist** | `drperera` | `dentist123` | Appointments schedule and patient dossiers |

---

## 🚀 How to Build & Run with Apache Tomcat

### Step 1: Run Unit Tests & Build WAR
Open PowerShell in the project root directory and run:
```powershell
mvn clean test
mvn clean package
```
*The packaged WAR file will be generated at `target/sunrise-dental.war`.*

### Step 2: Deploy to Apache Tomcat
1. Copy `target/sunrise-dental.war` to your Apache Tomcat `webapps/` folder (e.g. `C:\xampp\tomcat\webapps\` or your standalone Tomcat directory).
2. Start Apache Tomcat (`startup.bat`).
3. Open your browser and navigate to:
   ```
   http://localhost:8080/sunrise-dental/
   ```

---

## 🧪 Automated Testing (TDD - 20 Marks)
All unit tests are located in `src/test/java/com/sunrisedental/`:
- `AppointmentServiceTest.java`: Verifies double-booking conflict prevention and appointment generation.
- `BillingServiceTest.java`: Verifies 100% precision across all Strategy Pattern fee and discount calculations.
- `ValidationUtilTest.java`: Tests Sri Lankan mobile number validation and sanitization.
- `PasswordUtilTest.java`: Tests SHA-256 cryptographic hashing and verification.

To run the automated test suite at any time:
```powershell
mvn test
```
*Current test results: 14 tests run, 0 failures, 0 errors (100% pass rate).*
