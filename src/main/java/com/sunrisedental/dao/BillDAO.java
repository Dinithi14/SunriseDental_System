package com.sunrisedental.dao;

import com.sunrisedental.dto.BillReceiptDTO;
import com.sunrisedental.model.Bill;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BillDAO {
    Bill findById(int id);
    Bill findByBillNumber(String billNumber);
    Bill findByAppointmentId(int appointmentId);
    BillReceiptDTO getReceiptByBillId(int billId);
    BillReceiptDTO getReceiptByAppointmentId(int appointmentId);
    int create(Bill bill);
    boolean update(Bill bill);
    boolean updatePaymentStatus(int billId, String status, String paymentMethod);
    String generateNextBillNumber();
    BigDecimal calculateTotalRevenue();
    BigDecimal calculateTodayRevenue();
    List<Map<String, Object>> getDailyRevenueReport();
    List<Map<String, Object>> getAppointmentsByDentistReport();
    List<Map<String, Object>> getAppointmentsByTreatmentReport();
}
