package com.sunrisedental.dao.impl;

import com.sunrisedental.config.DatabaseConnection;
import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dto.AppointmentDetailDTO;
import com.sunrisedental.model.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentDAOImpl implements AppointmentDAO {

    private static final Logger LOGGER = Logger.getLogger(AppointmentDAOImpl.class.getName());

    private static final String BASE_SELECT_VIEW = 
            "SELECT a.id AS appointment_id, a.appointment_number, a.appointment_date, a.appointment_time, " +
            "a.status AS appointment_status, a.notes AS appointment_notes, " +
            "p.id AS patient_id, p.patient_code, p.full_name AS patient_name, p.contact_number AS patient_contact, " +
            "p.address AS patient_address, p.email AS patient_email, " +
            "d.id AS dentist_id, d.full_name AS dentist_name, d.specialization AS dentist_specialization, " +
            "d.consultation_fee, d.room_number, " +
            "t.id AS treatment_id, t.treatment_name, t.standard_cost AS treatment_cost, " +
            "b.id AS bill_id, b.bill_number, b.total_amount, b.payment_status, b.payment_method " +
            "FROM appointments a " +
            "JOIN patients p ON a.patient_id = p.id " +
            "JOIN dentists d ON a.dentist_id = d.id " +
            "JOIN treatments t ON a.treatment_id = t.id " +
            "LEFT JOIN bills b ON a.id = b.appointment_id ";

    @Override
    public Appointment findById(int id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding appointment by id: " + id, e);
        }
        return null;
    }

    @Override
    public Appointment findByAppointmentNumber(String appointmentNumber) {
        String sql = "SELECT * FROM appointments WHERE appointment_number = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding appointment by number: " + appointmentNumber, e);
        }
        return null;
    }

    @Override
    public AppointmentDetailDTO findDetailByNumber(String appointmentNumber) {
        String sql = BASE_SELECT_VIEW + "WHERE a.appointment_number = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapDetailRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding appointment detail by number: " + appointmentNumber, e);
        }
        return null;
    }

    @Override
    public AppointmentDetailDTO findDetailById(int id) {
        String sql = BASE_SELECT_VIEW + "WHERE a.id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapDetailRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding appointment detail by id: " + id, e);
        }
        return null;
    }

    @Override
    public List<AppointmentDetailDTO> findAllDetails() {
        List<AppointmentDetailDTO> list = new ArrayList<>();
        String sql = BASE_SELECT_VIEW + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapDetailRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all appointment details", e);
        }
        return list;
    }

    @Override
    public List<AppointmentDetailDTO> searchAppointments(String query, String status, Date fromDate, Date toDate, Integer dentistId) {
        List<AppointmentDetailDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT_VIEW).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (a.appointment_number LIKE ? OR p.full_name LIKE ? OR p.contact_number LIKE ? OR p.patient_code LIKE ?) ");
            String q = "%" + query.trim() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND a.status = ? ");
            params.add(status.trim());
        }

        if (dentistId != null && dentistId > 0) {
            sql.append("AND a.dentist_id = ? ");
            params.add(dentistId);
        }

        if (fromDate != null) {
            sql.append("AND a.appointment_date >= ? ");
            params.add(fromDate);
        }

        if (toDate != null) {
            sql.append("AND a.appointment_date <= ? ");
            params.add(toDate);
        }

        sql.append("ORDER BY a.appointment_date DESC, a.appointment_time DESC");

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDetailRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching appointments", e);
        }
        return list;
    }

    @Override
    public boolean checkDentistSlotConflict(int dentistId, Date date, Time time, Integer excludeAppointmentId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM appointments WHERE dentist_id = ? AND appointment_date = ? AND appointment_time = ? AND status != 'CANCELLED' ");
        if (excludeAppointmentId != null) {
            sql.append("AND id != ? ");
        }
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, dentistId);
            ps.setDate(2, date);
            ps.setTime(3, time);
            if (excludeAppointmentId != null) {
                ps.setInt(4, excludeAppointmentId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking appointment slot conflict", e);
        }
        return false;
    }

    @Override
    public int create(Appointment appointment) {
        if (appointment.getAppointmentNumber() == null || appointment.getAppointmentNumber().trim().isEmpty()) {
            appointment.setAppointmentNumber(generateNextAppointmentNumber());
        }
        String sql = "INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status, notes, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, appointment.getAppointmentNumber());
            ps.setInt(2, appointment.getPatientId());
            ps.setInt(3, appointment.getDentistId());
            ps.setInt(4, appointment.getTreatmentId());
            ps.setDate(5, appointment.getAppointmentDate());
            ps.setTime(6, appointment.getAppointmentTime());
            ps.setString(7, appointment.getStatus() != null ? appointment.getStatus() : "SCHEDULED");
            ps.setString(8, appointment.getNotes());
            if (appointment.getCreatedBy() != null) {
                ps.setInt(9, appointment.getCreatedBy());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        appointment.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting appointment", e);
        }
        return -1;
    }

    @Override
    public boolean update(Appointment appointment) {
        String sql = "UPDATE appointments SET dentist_id = ?, treatment_id = ?, appointment_date = ?, appointment_time = ?, status = ?, notes = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointment.getDentistId());
            ps.setInt(2, appointment.getTreatmentId());
            ps.setDate(3, appointment.getAppointmentDate());
            ps.setTime(4, appointment.getAppointmentTime());
            ps.setString(5, appointment.getStatus());
            ps.setString(6, appointment.getNotes());
            ps.setInt(7, appointment.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating appointment: " + appointment.getId(), e);
        }
        return false;
    }

    @Override
    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating appointment status", e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting appointment: " + id, e);
        }
        return false;
    }

    @Override
    public String generateNextAppointmentNumber() {
        String sql = "SELECT COUNT(*) FROM appointments";
        int count = 1;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1) + 1;
            }
        } catch (Exception e) {
            count = (int) (System.currentTimeMillis() % 10000);
        }
        return String.format("APT-%d-%04d", LocalDate.now().getYear(), count);
    }

    @Override
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error counting total appointments", e);
        }
        return 0;
    }

    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE status = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error counting appointments by status", e);
        }
        return 0;
    }

    @Override
    public int countToday() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error counting today's appointments", e);
        }
        return 0;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        return new Appointment(
                rs.getInt("id"),
                rs.getString("appointment_number"),
                rs.getInt("patient_id"),
                rs.getInt("dentist_id"),
                rs.getInt("treatment_id"),
                rs.getDate("appointment_date"),
                rs.getTime("appointment_time"),
                rs.getString("status"),
                rs.getString("notes"),
                (Integer) rs.getObject("created_by"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }

    private AppointmentDetailDTO mapDetailRow(ResultSet rs) throws SQLException {
        AppointmentDetailDTO dto = new AppointmentDetailDTO();
        dto.setAppointmentId(rs.getInt("appointment_id"));
        dto.setAppointmentNumber(rs.getString("appointment_number"));
        dto.setAppointmentDate(rs.getDate("appointment_date"));
        dto.setAppointmentTime(rs.getTime("appointment_time"));
        dto.setStatus(rs.getString("appointment_status"));
        dto.setNotes(rs.getString("appointment_notes"));

        dto.setPatientId(rs.getInt("patient_id"));
        dto.setPatientCode(rs.getString("patient_code"));
        dto.setPatientName(rs.getString("patient_name"));
        dto.setPatientContact(rs.getString("patient_contact"));
        dto.setPatientAddress(rs.getString("patient_address"));
        dto.setPatientEmail(rs.getString("patient_email"));

        dto.setDentistId(rs.getInt("dentist_id"));
        dto.setDentistName(rs.getString("dentist_name"));
        dto.setDentistSpecialization(rs.getString("dentist_specialization"));
        dto.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        dto.setRoomNumber(rs.getString("room_number"));

        dto.setTreatmentId(rs.getInt("treatment_id"));
        dto.setTreatmentName(rs.getString("treatment_name"));
        dto.setTreatmentCost(rs.getBigDecimal("treatment_cost"));

        int billId = rs.getInt("bill_id");
        if (!rs.wasNull()) {
            dto.setBillId(billId);
            dto.setBillNumber(rs.getString("bill_number"));
            dto.setTotalAmount(rs.getBigDecimal("total_amount"));
            dto.setPaymentStatus(rs.getString("payment_status"));
            dto.setPaymentMethod(rs.getString("payment_method"));
        }
        return dto;
    }
}
