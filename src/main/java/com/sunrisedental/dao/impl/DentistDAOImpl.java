package com.sunrisedental.dao.impl;

import com.sunrisedental.config.DatabaseConnection;
import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.model.Dentist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DentistDAOImpl implements DentistDAO {

    private static final Logger LOGGER = Logger.getLogger(DentistDAOImpl.class.getName());

    @Override
    public Dentist findById(int id) {
        String sql = "SELECT * FROM dentists WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding dentist by id: " + id, e);
        }
        return null;
    }

    @Override
    public List<Dentist> findAll() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT * FROM dentists ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all dentists", e);
        }
        return list;
    }

    @Override
    public List<Dentist> findAllActive() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT * FROM dentists WHERE active = TRUE ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching active dentists", e);
        }
        return list;
    }

    @Override
    public boolean create(Dentist dentist) {
        String sql = "INSERT INTO dentists (dentist_code, full_name, specialization, contact_number, email, consultation_fee, available_days, room_number, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dentist.getDentistCode());
            ps.setString(2, dentist.getFullName());
            ps.setString(3, dentist.getSpecialization());
            ps.setString(4, dentist.getContactNumber());
            ps.setString(5, dentist.getEmail());
            ps.setBigDecimal(6, dentist.getConsultationFee());
            ps.setString(7, dentist.getAvailableDays());
            ps.setString(8, dentist.getRoomNumber());
            ps.setBoolean(9, dentist.isActive());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) dentist.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating dentist", e);
        }
        return false;
    }

    @Override
    public boolean update(Dentist dentist) {
        String sql = "UPDATE dentists SET full_name = ?, specialization = ?, contact_number = ?, email = ?, consultation_fee = ?, available_days = ?, room_number = ?, active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentist.getFullName());
            ps.setString(2, dentist.getSpecialization());
            ps.setString(3, dentist.getContactNumber());
            ps.setString(4, dentist.getEmail());
            ps.setBigDecimal(5, dentist.getConsultationFee());
            ps.setString(6, dentist.getAvailableDays());
            ps.setString(7, dentist.getRoomNumber());
            ps.setBoolean(8, dentist.isActive());
            ps.setInt(9, dentist.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating dentist: " + dentist.getId(), e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "UPDATE dentists SET active = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deactivating dentist: " + id, e);
        }
        return false;
    }

    private Dentist mapRow(ResultSet rs) throws SQLException {
        return new Dentist(
                rs.getInt("id"),
                rs.getString("dentist_code"),
                rs.getString("full_name"),
                rs.getString("specialization"),
                rs.getString("contact_number"),
                rs.getString("email"),
                rs.getBigDecimal("consultation_fee"),
                rs.getString("available_days"),
                rs.getString("room_number"),
                rs.getBoolean("active"),
                rs.getTimestamp("created_at")
        );
    }
}
