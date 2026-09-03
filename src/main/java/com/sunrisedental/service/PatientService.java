package com.sunrisedental.service;

import com.sunrisedental.dao.DAOFactory;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.model.Patient;
import com.sunrisedental.util.ValidationUtil;

import java.util.List;

public class PatientService {

    private final PatientDAO patientDAO;

    public PatientService() {
        this.patientDAO = DAOFactory.getPatientDAO();
    }

    public PatientService(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    public Patient getPatientById(int id) {
        return patientDAO.findById(id);
    }

    public Patient getPatientByCode(String code) {
        return patientDAO.findByPatientCode(code);
    }

    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }

    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return patientDAO.findAll();
        }
        return patientDAO.search(query.trim());
    }

    public int registerPatient(Patient patient) throws IllegalArgumentException {
        // Business Validation
        if (!ValidationUtil.isNotEmpty(patient.getFullName())) {
            throw new IllegalArgumentException("Patient full name is mandatory.");
        }
        if (!ValidationUtil.isNotEmpty(patient.getContactNumber())) {
            throw new IllegalArgumentException("Contact number is mandatory.");
        }
        if (!ValidationUtil.isValidPhoneNumber(patient.getContactNumber())) {
            throw new IllegalArgumentException("Invalid Sri Lankan contact number format (e.g. 0771234567).");
        }
        if (!ValidationUtil.isNotEmpty(patient.getAddress())) {
            throw new IllegalArgumentException("Patient address is mandatory.");
        }
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty() && !ValidationUtil.isValidEmail(patient.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        return patientDAO.create(patient);
    }

    public boolean updatePatient(Patient patient) throws IllegalArgumentException {
        if (!ValidationUtil.isNotEmpty(patient.getFullName()) || !ValidationUtil.isNotEmpty(patient.getContactNumber())) {
            throw new IllegalArgumentException("Full name and contact number cannot be empty.");
        }
        return patientDAO.update(patient);
    }

    public boolean deletePatient(int id) {
        return patientDAO.delete(id);
    }
}
