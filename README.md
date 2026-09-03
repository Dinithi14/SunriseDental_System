# Sunrise Dental Clinic Management System

An enterprise Patient & Appointment Management System developed for **Sunrise Dental Clinic (Colombo)** to manage appointments, patient records, clinical schedules, billing, and reporting.

---

## Features

- **User Authentication & Role-Based Access:** Secure login sessions for Admin, Receptionist, and Dentist accounts.
- **Patient Management:** Comprehensive patient profile registration with contact details, emergency contacts, and medical history.
- **Appointment Scheduling:** Real-time scheduling with conflict detection to prevent double bookings across doctor suites.
- **Search & Dossier Lookup:** Instant lookup by appointment reference number, patient name, contact number, or attending doctor.
- **Billing & Invoice System:** Dynamic calculation of treatment fees, consultation fees, and payment schemes with printable receipts.
- **Executive Analytics:** Financial summaries, daily income logs, doctor workload distribution, and treatment demand statistics.
- **Staff User Guide:** Step-by-step Standard Operating Procedures (SOP) built directly into the system.

---

## Technology Stack

- **Backend:** Java (Servlets, DAO, Services, DTOs, JDBC)
- **Frontend:** HTML5, CSS3, JavaScript, JSP
- **Build Tool:** Apache Maven
- **Web Server:** Apache Tomcat 10.1+ (Jakarta EE)
- **Database:** MySQL (XAMPP compatible)
- **Testing:** JUnit 5

---

## Database Setup (MySQL / XAMPP)

1. Start **MySQL** in the XAMPP Control Panel.
2. Open phpMyAdmin (`http://localhost/phpmyadmin`) or MySQL CLI.
3. Import the database schema script:
   ```
   database/schema.sql
   ```
4. The `sunrise_dental_db` database will be created with all tables, triggers, views, and initial data.

---

## Build & Deployment Instructions

### 1. Build WAR Package with Maven
```bash
mvn clean package
```
*(The compiled WAR file will be generated at `target/sunrise-dental.war`)*

### 2. Deploy to Apache Tomcat
1. Copy `target/sunrise-dental.war` to the Tomcat `webapps/` folder.
2. Start Apache Tomcat (`startup.bat` on Windows or `catalina.sh start` on Linux/macOS).
3. Access the application in your browser:
   ```
   http://localhost:8080/sunrise-dental/
   ```

---

## Automated Testing

Run the automated test suite with Maven:
```bash
mvn test
```
All unit tests verify appointment conflict avoidance, billing calculations, and input validations.
