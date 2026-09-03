-- ==========================================================
-- SUNRISE DENTAL CLINIC - COLOMBO
-- Relational Database Schema & Stored Procedures
-- ==========================================================

CREATE DATABASE IF NOT EXISTS sunrise_dental_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sunrise_dental_db;

-- 1. USERS & AUTHENTICATION TABLE
DROP TABLE IF EXISTS sms_email_notifications;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS treatments;
DROP TABLE IF EXISTS dentists;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'RECEPTIONIST', 'DENTIST') NOT NULL DEFAULT 'RECEPTIONIST',
    email VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. PATIENTS TABLE
CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    nic_passport VARCHAR(30) NULL,
    contact_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NULL,
    address TEXT NOT NULL,
    date_of_birth DATE NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL DEFAULT 'MALE',
    blood_group VARCHAR(10) DEFAULT 'N/A',
    emergency_contact VARCHAR(20) NULL,
    medical_history TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. DENTISTS TABLE
CREATE TABLE dentists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dentist_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 2500.00,
    available_days VARCHAR(100) NOT NULL DEFAULT 'Monday - Saturday',
    room_number VARCHAR(20) NOT NULL DEFAULT 'Room 101',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4. TREATMENTS TABLE
CREATE TABLE treatments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    treatment_code VARCHAR(20) NOT NULL UNIQUE,
    treatment_name VARCHAR(120) NOT NULL,
    description TEXT NULL,
    standard_cost DECIMAL(10,2) NOT NULL,
    estimated_minutes INT NOT NULL DEFAULT 30,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 5. APPOINTMENTS TABLE
CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(30) NOT NULL UNIQUE,
    patient_id INT NOT NULL,
    dentist_id INT NOT NULL,
    treatment_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    notes TEXT NULL,
    created_by INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    FOREIGN KEY (dentist_id) REFERENCES dentists(id) ON DELETE RESTRICT,
    FOREIGN KEY (treatment_id) REFERENCES treatments(id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    -- Prevent double booking on exact date/time with the same dentist
    CONSTRAINT uq_dentist_slot UNIQUE (dentist_id, appointment_date, appointment_time)
) ENGINE=InnoDB;

-- 6. BILLS & INVOICES TABLE
CREATE TABLE bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(30) NOT NULL UNIQUE,
    appointment_id INT NOT NULL,
    patient_id INT NOT NULL,
    treatment_cost DECIMAL(10,2) NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    additional_charges DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_strategy VARCHAR(50) NOT NULL DEFAULT 'STANDARD',
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('PENDING', 'PAID', 'CANCELLED') NOT NULL DEFAULT 'PAID',
    payment_method ENUM('CASH', 'CARD', 'INSURANCE', 'ONLINE') NOT NULL DEFAULT 'CASH',
    billing_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT NULL,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE RESTRICT,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 7. AUDIT LOGS TABLE
CREATE TABLE audit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    action_type VARCHAR(50) NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    record_id INT NULL,
    performed_by VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    log_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 8. SIMULATED NOTIFICATION LOGS (SMS / EMAIL)
CREATE TABLE sms_email_notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    recipient_contact VARCHAR(30) NOT NULL,
    recipient_email VARCHAR(100) NULL,
    notification_type ENUM('SMS', 'EMAIL') NOT NULL,
    message TEXT NOT NULL,
    status ENUM('SENT', 'QUEUED', 'FAILED') NOT NULL DEFAULT 'SENT',
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================================================
-- ADVANCED DATABASE FEATURES: TRIGGERS, VIEWS & PROCEDURES
-- ==========================================================

-- Trigger: Audit log whenever a new appointment is booked
DELIMITER $$
CREATE TRIGGER trg_after_appointment_insert
AFTER INSERT ON appointments
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (action_type, table_name, record_id, performed_by, description)
    VALUES ('INSERT', 'appointments', NEW.id, 'SYSTEM', CONCAT('New appointment booked: ', NEW.appointment_number, ' on ', NEW.appointment_date, ' at ', NEW.appointment_time));
END$$

-- Trigger: Audit log when appointment is updated or completed
CREATE TRIGGER trg_after_appointment_update
AFTER UPDATE ON appointments
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (action_type, table_name, record_id, performed_by, description)
    VALUES ('UPDATE', 'appointments', NEW.id, 'SYSTEM', CONCAT('Appointment ', NEW.appointment_number, ' status changed to ', NEW.status));
END$$
DELIMITER ;

-- View: Complete Appointment Details View (joins Patient, Dentist, Treatment, Bill)
CREATE OR REPLACE VIEW view_appointment_details AS
SELECT 
    a.id AS appointment_id,
    a.appointment_number,
    a.appointment_date,
    a.appointment_time,
    a.status AS appointment_status,
    a.notes AS appointment_notes,
    p.id AS patient_id,
    p.patient_code,
    p.full_name AS patient_name,
    p.contact_number AS patient_contact,
    p.address AS patient_address,
    p.email AS patient_email,
    d.id AS dentist_id,
    d.full_name AS dentist_name,
    d.specialization AS dentist_specialization,
    d.consultation_fee,
    d.room_number,
    t.id AS treatment_id,
    t.treatment_name,
    t.standard_cost AS treatment_cost,
    b.id AS bill_id,
    b.bill_number,
    b.total_amount,
    b.payment_status,
    b.payment_method
FROM appointments a
JOIN patients p ON a.patient_id = p.id
JOIN dentists d ON a.dentist_id = d.id
JOIN treatments t ON a.treatment_id = t.id
LEFT JOIN bills b ON a.id = b.appointment_id;

-- View: Daily Clinic Revenue & Statistics Summary View
CREATE OR REPLACE VIEW view_daily_revenue_summary AS
SELECT 
    DATE(b.billing_date) AS report_date,
    COUNT(b.id) AS total_bills,
    SUM(b.treatment_cost) AS total_treatment_income,
    SUM(b.consultation_fee) AS total_consultation_income,
    SUM(b.discount_amount) AS total_discounts_given,
    SUM(b.total_amount) AS total_net_revenue
FROM bills b
WHERE b.payment_status = 'PAID'
GROUP BY DATE(b.billing_date);

-- Stored Procedure: Generate Next Appointment Number
DELIMITER $$
CREATE PROCEDURE sp_get_next_appointment_number(OUT next_app_no VARCHAR(30))
BEGIN
    DECLARE total_count INT;
    SELECT COUNT(*) + 1 INTO total_count FROM appointments;
    SET next_app_no = CONCAT('APT-', YEAR(CURDATE()), '-', LPAD(total_count, 4, '0'));
END$$
DELIMITER ;

-- ==========================================================
-- SAMPLE SEED DATA
-- Default Credentials:
-- Admin: admin / admin123
-- Receptionist: receptionist / recep123
-- Dentist: drperera / dentist123
-- ==========================================================

-- Seed Users (Passwords hashed using SHA-256 with salt)
-- Salt: "sunrise_salt_2026"
-- admin123 -> SHA256("admin123sunrise_salt_2026") = 8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918 (handled by app security utility)
INSERT INTO users (username, password_hash, salt, full_name, role, email) VALUES
('admin', '62bb545326a6f03315b188976ee9ebf5d28524c4c40235960cf80b73d4365bb2', 'sunrise_salt_2026', 'Mr. Kamal Gunaratne (Clinic Director)', 'ADMIN', 'admin@sunrisedental.lk'),
('receptionist', 'afb8661633954c19070581bc83af302be4e1d6ab4e96ec2ef89309f2020fc482', 'sunrise_salt_2026', 'Ms. Anoma Wickramasinghe (Head Receptionist)', 'RECEPTIONIST', 'reception@sunrisedental.lk'),
('drperera', 'f33ac4aff59bd525abc5784bd054dd7e90bf602489741d1a6fa744c9026556c8', 'sunrise_salt_2026', 'Dr. Ruwan Perera (BDS, MS Orthodontics)', 'DENTIST', 'dr.perera@sunrisedental.lk');

-- Seed Dentists
INSERT INTO dentists (dentist_code, full_name, specialization, contact_number, email, consultation_fee, available_days, room_number) VALUES
('DEN-001', 'Dr. Ruwan Perera', 'Orthodontist & Dental Surgeon', '0771234567', 'dr.ruwan@sunrisedental.lk', 2500.00, 'Monday, Wednesday, Friday (09:00 - 17:00)', 'Dental Suite 1'),
('DEN-002', 'Dr. Chathuri Silva', 'Periodontist & Cosmetic Dentistry', '0719876543', 'dr.chathuri@sunrisedental.lk', 3000.00, 'Tuesday, Thursday, Saturday (10:00 - 18:00)', 'Dental Suite 2'),
('DEN-003', 'Dr. Nuwan Fernando', 'Oral & Maxillofacial Surgeon', '0765551234', 'dr.nuwan@sunrisedental.lk', 3500.00, 'Monday to Friday (14:00 - 20:00)', 'Dental Suite 3'),
('DEN-004', 'Dr. Sanduni Jayawardena', 'Pediatric Dental Specialist', '0723334444', 'dr.sanduni@sunrisedental.lk', 2200.00, 'Wednesday, Saturday, Sunday (09:00 - 15:00)', 'Dental Suite 4');

-- Seed Treatments
INSERT INTO treatments (treatment_code, treatment_name, description, standard_cost, estimated_minutes) VALUES
('TRT-001', 'Dental Consultation & Oral Examination', 'Comprehensive oral inspection, gum health assessment, and dental plan', 1500.00, 20),
('TRT-002', 'Teeth Cleaning & Ultrasonic Scaling', 'Removal of plaque, tartar, and calculus polishing for oral hygiene', 4500.00, 30),
('TRT-003', 'Composite Tooth Filling', 'Tooth-colored resin filling for dental cavity restoration', 6000.00, 45),
('TRT-004', 'Root Canal Therapy (RCT)', 'Endodontic treatment for infected pulp with sealing', 22000.00, 60),
('TRT-005', 'Tooth Extraction (Simple)', 'Routine pain-free tooth removal under local anesthesia', 5000.00, 30),
('TRT-006', 'Surgical Wisdom Tooth Removal', 'Surgical impaction extraction with suture and post-op care', 18000.00, 60),
('TRT-007', 'Teeth Whitening (Laser Bleaching)', 'Professional cosmetic laser teeth shade lightening', 28000.00, 60),
('TRT-008', 'Porcelain Dental Crown / Bridge', 'Custom ceramic crown cap for tooth protection and aesthetics', 35000.00, 45);

-- Seed Patients
INSERT INTO patients (patient_code, full_name, nic_passport, contact_number, email, address, date_of_birth, gender, blood_group, emergency_contact, medical_history) VALUES
('PAT-001', 'Kasun Mendis', '199012345678', '0774441122', 'kasun.m@gmail.com', 'No. 45/2, Galle Road, Colombo 03', '1990-05-14', 'MALE', 'O+', '0774441100', 'Mild asthma, no known drug allergies'),
('PAT-002', 'Dilani Senanayake', '198565432100', '0712223344', 'dilani.s@yahoo.com', 'No. 12, Havelock Road, Colombo 05', '1985-11-28', 'FEMALE', 'A+', '0712223300', 'Penicillin allergy noted'),
('PAT-003', 'Thilina Rathnayake', '199834567890', '0768889900', 'thilina.r@outlook.com', 'No. 88, Kandy Road, Kelaniya', '1998-03-02', 'MALE', 'B+', '0768889911', 'None'),
('PAT-004', 'Malkanthi Fernando', '196277889900', '0725556677', 'malkanthi@gmail.com', 'No. 104, High Level Road, Nugegoda', '1962-08-19', 'FEMALE', 'AB+', '0725556600', 'Hypertension under medication');

-- Seed Appointments
INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status, notes, created_by) VALUES
('APT-2026-0001', 1, 1, 2, '2026-09-05', '09:30:00', 'SCHEDULED', 'Routine scaling and tartar removal', 2),
('APT-2026-0002', 2, 2, 4, '2026-09-05', '10:30:00', 'SCHEDULED', 'Upper molar pain, scheduled for RCT session 1', 2),
('APT-2026-0003', 3, 3, 3, '2026-09-06', '14:00:00', 'SCHEDULED', 'Lower premolar filling', 2),
('APT-2026-0004', 4, 1, 1, '2026-09-04', '11:00:00', 'COMPLETED', 'General oral consultation and checkup', 2);

-- Seed Bills
INSERT INTO bills (bill_number, appointment_id, patient_id, treatment_cost, consultation_fee, additional_charges, discount_amount, discount_strategy, tax_amount, total_amount, payment_status, payment_method, notes) VALUES
('INV-2026-0001', 4, 4, 1500.00, 2500.00, 0.00, 400.00, 'SENIOR_DISCOUNT', 0.00, 3600.00, 'PAID', 'CASH', 'Senior citizen discount 10% applied on consultation + treatment');
