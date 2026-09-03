package com.sunrisedental.dao.impl;

import com.sunrisedental.config.DatabaseConnection;
import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.dto.BillReceiptDTO;
import com.sunrisedental.model.Bill;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillDAOImpl implements BillDAO {

    private static final Logger LOGGER = Logger.getLogger(BillDAOImpl.class.getName());

    private static final String RECEIPT_QUERY = 
            "SELECT b.id AS bill_id, b.bill_number, b.billing_date, b.payment_status, b.payment_method, " +
            "b.treatment_cost, b.consultation_fee, b.additional_charges, b.discount_amount, " +
            "b.discount_strategy, b.tax_amount, b.total_amount, b.notes AS bill_notes, " +
            "a.appointment_number, a.appointment_date, a.appointment_time, " +
            "p.patient_code, p.full_name AS patient_name, p.contact_number AS patient_contact, " +
            "p.address AS patient_address, p.email AS patient_email, " +
            "d.full_name AS dentist_name, d.specialization AS dentist_specialization, d.room_number, " +
            "t.treatment_name " +
            "FROM bills b " +
            "JOIN appointments a ON b.appointment_id = a.id " +
            "JOIN patients p ON b.patient_id = p.id " +
            "JOIN dentists d ON a.dentist_id = d.id " +
            "JOIN treatments t ON a.treatment_id = t.id ";

    @Override
    public Bill findById(int id) {
        String sql = "SELECT * FROM bills WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bill by id: " + id, e);
        }
        return null;
    }

    @Override
    public Bill findByBillNumber(String billNumber) {
        String sql = "SELECT * FROM bills WHERE bill_number = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, billNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bill by number: " + billNumber, e);
        }
        return null;
    }

    @Override
    public Bill findByAppointmentId(int appointmentId) {
        String sql = "SELECT * FROM bills WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bill by appointment id: " + appointmentId, e);
        }
        return null;
    }

    @Override
    public BillReceiptDTO getReceiptByBillId(int billId) {
        String sql = RECEIPT_QUERY + "WHERE b.id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapReceiptRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching receipt by bill id: " + billId, e);
        }
        return null;
    }

    @Override
    public BillReceiptDTO getReceiptByBillNumber(String billNumber) {
        String sql = RECEIPT_QUERY + "WHERE b.bill_number = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, billNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapReceiptRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching receipt by bill number: " + billNumber, e);
        }
        return null;
    }

    @Override
    public BillReceiptDTO getReceiptByAppointmentId(int appointmentId) {
        String sql = RECEIPT_QUERY + "WHERE b.appointment_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapReceiptRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching receipt by appointment id: " + appointmentId, e);
        }
        return null;
    }

    @Override
    public int create(Bill bill) {
        if (bill.getBillNumber() == null || bill.getBillNumber().trim().isEmpty()) {
            bill.setBillNumber(generateNextBillNumber());
        }
        String sql = "INSERT INTO bills (bill_number, appointment_id, patient_id, treatment_cost, consultation_fee, additional_charges, discount_amount, discount_strategy, tax_amount, total_amount, payment_status, payment_method, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bill.getBillNumber());
            ps.setInt(2, bill.getAppointmentId());
            ps.setInt(3, bill.getPatientId());
            ps.setBigDecimal(4, bill.getTreatmentCost());
            ps.setBigDecimal(5, bill.getConsultationFee());
            ps.setBigDecimal(6, bill.getAdditionalCharges());
            ps.setBigDecimal(7, bill.getDiscountAmount());
            ps.setString(8, bill.getDiscountStrategy() != null ? bill.getDiscountStrategy() : "STANDARD");
            ps.setBigDecimal(9, bill.getTaxAmount());
            ps.setBigDecimal(10, bill.getTotalAmount());
            ps.setString(11, bill.getPaymentStatus() != null ? bill.getPaymentStatus() : "PAID");
            ps.setString(12, bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "CASH");
            ps.setString(13, bill.getNotes());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        bill.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating bill record", e);
        }
        return -1;
    }

    @Override
    public boolean update(Bill bill) {
        String sql = "UPDATE bills SET treatment_cost = ?, consultation_fee = ?, additional_charges = ?, discount_amount = ?, discount_strategy = ?, tax_amount = ?, total_amount = ?, payment_status = ?, payment_method = ?, notes = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, bill.getTreatmentCost());
            ps.setBigDecimal(2, bill.getConsultationFee());
            ps.setBigDecimal(3, bill.getAdditionalCharges());
            ps.setBigDecimal(4, bill.getDiscountAmount());
            ps.setString(5, bill.getDiscountStrategy());
            ps.setBigDecimal(6, bill.getTaxAmount());
            ps.setBigDecimal(7, bill.getTotalAmount());
            ps.setString(8, bill.getPaymentStatus());
            ps.setString(9, bill.getPaymentMethod());
            ps.setString(10, bill.getNotes());
            ps.setInt(11, bill.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating bill", e);
        }
        return false;
    }

    @Override
    public boolean updatePaymentStatus(int billId, String status, String paymentMethod) {
        String sql = "UPDATE bills SET payment_status = ?, payment_method = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, paymentMethod);
            ps.setInt(3, billId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating payment status", e);
        }
        return false;
    }

    @Override
    public String generateNextBillNumber() {
        String sql = "SELECT COUNT(*) FROM bills";
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
        return String.format("INV-%d-%04d", LocalDate.now().getYear(), count);
    }

    @Override
    public BigDecimal calculateTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM bills WHERE payment_status = 'PAID'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                BigDecimal sum = rs.getBigDecimal(1);
                return sum != null ? sum : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error calculating total revenue", e);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTodayRevenue() {
        String sql = "SELECT SUM(total_amount) FROM bills WHERE payment_status = 'PAID' AND DATE(billing_date) = CURDATE()";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                BigDecimal sum = rs.getBigDecimal(1);
                return sum != null ? sum : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error calculating today revenue", e);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<Map<String, Object>> getDailyRevenueReport() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT DATE(billing_date) AS bill_day, COUNT(id) AS bill_count, SUM(total_amount) AS revenue " +
                     "FROM bills WHERE payment_status = 'PAID' GROUP BY DATE(billing_date) ORDER BY bill_day DESC LIMIT 10";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("date", rs.getString("bill_day"));
                map.put("count", rs.getInt("bill_count"));
                map.put("revenue", rs.getBigDecimal("revenue"));
                list.add(map);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error getting daily revenue report", e);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getAppointmentsByDentistReport() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT d.full_name AS dentist_name, d.specialization, COUNT(a.id) AS appointment_count " +
                     "FROM dentists d LEFT JOIN appointments a ON d.id = a.dentist_id " +
                     "GROUP BY d.id, d.full_name, d.specialization ORDER BY appointment_count DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("dentistName", rs.getString("dentist_name"));
                map.put("specialization", rs.getString("specialization"));
                map.put("appointmentCount", rs.getInt("appointment_count"));
                list.add(map);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error getting dentist report", e);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getAppointmentsByTreatmentReport() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT t.treatment_name, COUNT(a.id) AS count, SUM(t.standard_cost) AS total_value " +
                     "FROM treatments t LEFT JOIN appointments a ON t.id = a.treatment_id " +
                     "GROUP BY t.id, t.treatment_name ORDER BY count DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("treatmentName", rs.getString("treatment_name"));
                map.put("count", rs.getInt("count"));
                map.put("totalValue", rs.getBigDecimal("total_value"));
                list.add(map);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error getting treatment report", e);
        }
        return list;
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        return new Bill(
                rs.getInt("id"),
                rs.getString("bill_number"),
                rs.getInt("appointment_id"),
                rs.getInt("patient_id"),
                rs.getBigDecimal("treatment_cost"),
                rs.getBigDecimal("consultation_fee"),
                rs.getBigDecimal("additional_charges"),
                rs.getBigDecimal("discount_amount"),
                rs.getString("discount_strategy"),
                rs.getBigDecimal("tax_amount"),
                rs.getBigDecimal("total_amount"),
                rs.getString("payment_status"),
                rs.getString("payment_method"),
                rs.getTimestamp("billing_date"),
                rs.getString("notes")
        );
    }

    private BillReceiptDTO mapReceiptRow(ResultSet rs) throws SQLException {
        BillReceiptDTO dto = new BillReceiptDTO();
        dto.setBillNumber(rs.getString("bill_number"));
        dto.setBillingDate(rs.getTimestamp("billing_date"));
        dto.setPaymentStatus(rs.getString("payment_status"));
        dto.setPaymentMethod(rs.getString("payment_method"));
        dto.setTreatmentCost(rs.getBigDecimal("treatment_cost"));
        dto.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        dto.setAdditionalCharges(rs.getBigDecimal("additional_charges"));
        dto.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        dto.setDiscountStrategy(rs.getString("discount_strategy"));
        dto.setTaxAmount(rs.getBigDecimal("tax_amount"));
        dto.setTotalAmount(rs.getBigDecimal("total_amount"));
        dto.setNotes(rs.getString("bill_notes"));

        dto.setAppointmentNumber(rs.getString("appointment_number"));
        dto.setAppointmentDate(rs.getString("appointment_date"));
        dto.setAppointmentTime(rs.getString("appointment_time"));

        dto.setPatientCode(rs.getString("patient_code"));
        dto.setPatientName(rs.getString("patient_name"));
        dto.setPatientContact(rs.getString("patient_contact"));
        dto.setPatientAddress(rs.getString("patient_address"));
        dto.setPatientEmail(rs.getString("patient_email"));

        dto.setDentistName(rs.getString("dentist_name"));
        dto.setDentistSpecialization(rs.getString("dentist_specialization"));
        dto.setRoomNumber(rs.getString("room_number"));

        dto.setTreatmentName(rs.getString("treatment_name"));
        return dto;
    }
}
