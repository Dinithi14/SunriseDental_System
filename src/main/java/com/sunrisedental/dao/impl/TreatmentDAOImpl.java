package com.sunrisedental.dao.impl;

import com.sunrisedental.config.DatabaseConnection;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Treatment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TreatmentDAOImpl implements TreatmentDAO {

    private static final Logger LOGGER = Logger.getLogger(TreatmentDAOImpl.class.getName());

    @Override
    public Treatment findById(int id) {
        String sql = "SELECT * FROM treatments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding treatment by id: " + id, e);
        }
        return null;
    }

    @Override
    public List<Treatment> findAll() {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT * FROM treatments ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all treatments", e);
        }
        return list;
    }

    @Override
    public List<Treatment> findAllActive() {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT * FROM treatments WHERE active = TRUE ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching active treatments", e);
        }
        return list;
    }

    @Override
    public boolean create(Treatment treatment) {
        String sql = "INSERT INTO treatments (treatment_code, treatment_name, description, standard_cost, estimated_minutes, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, treatment.getTreatmentCode());
            ps.setString(2, treatment.getTreatmentName());
            ps.setString(3, treatment.getDescription());
            ps.setBigDecimal(4, treatment.getStandardCost());
            ps.setInt(5, treatment.getEstimatedMinutes());
            ps.setBoolean(6, treatment.isActive());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) treatment.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating treatment", e);
        }
        return false;
    }

    @Override
    public boolean update(Treatment treatment) {
        String sql = "UPDATE treatments SET treatment_name = ?, description = ?, standard_cost = ?, estimated_minutes = ?, active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, treatment.getTreatmentName());
            ps.setString(2, treatment.getDescription());
            ps.setBigDecimal(3, treatment.getStandardCost());
            ps.setInt(4, treatment.getEstimatedMinutes());
            ps.setBoolean(5, treatment.isActive());
            ps.setInt(6, treatment.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating treatment: " + treatment.getId(), e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "UPDATE treatments SET active = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deactivating treatment: " + id, e);
        }
        return false;
    }

    private Treatment mapRow(ResultSet rs) throws SQLException {
        return new Treatment(
                rs.getInt("id"),
                rs.getString("treatment_code"),
                rs.getString("treatment_name"),
                rs.getString("description"),
                rs.getBigDecimal("standard_cost"),
                rs.getInt("estimated_minutes"),
                rs.getBoolean("active"),
                rs.getTimestamp("created_at")
        );
    }
}
