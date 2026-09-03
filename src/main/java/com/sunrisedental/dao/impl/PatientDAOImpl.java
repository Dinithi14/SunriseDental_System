package com.sunrisedental.dao.impl;

import com.sunrisedental.config.DatabaseConnection;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientDAOImpl implements PatientDAO {

    private static final Logger LOGGER = Logger.getLogger(PatientDAOImpl.class.getName());

    @Override
    public Patient findById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding patient by id: " + id, e);
        }
        return null;
    }

    @Override
    public Patient findByPatientCode(String code) {
        String sql = "SELECT * FROM patients WHERE patient_code = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding patient by code: " + code, e);
        }
        return null;
    }

    @Override
    public List<Patient> findAll() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all patients", e);
        }
        return list;
    }

    @Override
    public List<Patient> search(String query) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE full_name LIKE ? OR contact_number LIKE ? OR patient_code LIKE ? OR nic_passport LIKE ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = "%" + query.trim() + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            ps.setString(4, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching patients with query: " + query, e);
        }
        return list;
    }

    @Override
    public int create(Patient patient) {
        if (patient.getPatientCode() == null || patient.getPatientCode().trim().isEmpty()) {
            patient.setPatientCode(generateNextPatientCode());
        }
        String sql = "INSERT INTO patients (patient_code, full_name, nic_passport, contact_number, email, address, date_of_birth, gender, blood_group, emergency_contact, medical_history) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, patient.getPatientCode());
            ps.setString(2, patient.getFullName());
            ps.setString(3, patient.getNicPassport());
            ps.setString(4, patient.getContactNumber());
            ps.setString(5, patient.getEmail());
            ps.setString(6, patient.getAddress());
            ps.setDate(7, patient.getDateOfBirth());
            ps.setString(8, patient.getGender() != null ? patient.getGender() : "MALE");
            ps.setString(9, patient.getBloodGroup());
            ps.setString(10, patient.getEmergencyContact());
            ps.setString(11, patient.getMedicalHistory());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        patient.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating patient", e);
        }
        return -1;
    }

    @Override
    public boolean update(Patient patient) {
        String sql = "UPDATE patients SET full_name = ?, nic_passport = ?, contact_number = ?, email = ?, address = ?, date_of_birth = ?, gender = ?, blood_group = ?, emergency_contact = ?, medical_history = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getFullName());
            ps.setString(2, patient.getNicPassport());
            ps.setString(3, patient.getContactNumber());
            ps.setString(4, patient.getEmail());
            ps.setString(5, patient.getAddress());
            ps.setDate(6, patient.getDateOfBirth());
            ps.setString(7, patient.getGender());
            ps.setString(8, patient.getBloodGroup());
            ps.setString(9, patient.getEmergencyContact());
            ps.setString(10, patient.getMedicalHistory());
            ps.setInt(11, patient.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating patient: " + patient.getId(), e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting patient: " + id, e);
        }
        return false;
    }

    @Override
    public String generateNextPatientCode() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("PAT-%03d", count);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Fallback patient code generation due to: " + e.getMessage());
        }
        return "PAT-" + System.currentTimeMillis() % 1000;
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getInt("id"),
                rs.getString("patient_code"),
                rs.getString("full_name"),
                rs.getString("nic_passport"),
                rs.getString("contact_number"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getDate("date_of_birth"),
                rs.getString("gender"),
                rs.getString("blood_group"),
                rs.getString("emergency_contact"),
                rs.getString("medical_history"),
                rs.getTimestamp("created_at")
        );
    }
}
