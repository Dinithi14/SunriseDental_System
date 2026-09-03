package com.sunrisedental.service;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.dao.DAOFactory;
import com.sunrisedental.dto.AppointmentDetailDTO;
import com.sunrisedental.dto.BillReceiptDTO;
import com.sunrisedental.model.Bill;
import com.sunrisedental.service.strategy.BillingStrategy;
import com.sunrisedental.service.strategy.BillingStrategyFactory;

import java.math.BigDecimal;
import java.util.logging.Logger;

public class BillingService {

    private static final Logger LOGGER = Logger.getLogger(BillingService.class.getName());

    private final BillDAO billDAO;
    private final AppointmentDAO appointmentDAO;

    public BillingService() {
        this.billDAO = DAOFactory.getBillDAO();
        this.appointmentDAO = DAOFactory.getAppointmentDAO();
    }

    public BillingService(BillDAO billDAO, AppointmentDAO appointmentDAO) {
        this.billDAO = billDAO;
        this.appointmentDAO = appointmentDAO;
    }

    /**
     * Calculates bill breakdown dynamically without saving to DB (for real-time preview in UI).
     */
    public Bill calculateBillPreview(BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal additionalCharges, String strategyName) {
        if (treatmentCost == null) treatmentCost = BigDecimal.ZERO;
        if (consultationFee == null) consultationFee = BigDecimal.ZERO;
        if (additionalCharges == null) additionalCharges = BigDecimal.ZERO;

        BillingStrategy strategy = BillingStrategyFactory.getStrategy(strategyName);
        BigDecimal discount = strategy.calculateDiscount(treatmentCost, consultationFee, additionalCharges);
        BigDecimal subtotal = treatmentCost.add(consultationFee).add(additionalCharges).subtract(discount);
        BigDecimal tax = strategy.calculateTax(subtotal);
        BigDecimal total = strategy.calculateTotal(treatmentCost, consultationFee, additionalCharges);

        Bill bill = new Bill();
        bill.setTreatmentCost(treatmentCost);
        bill.setConsultationFee(consultationFee);
        bill.setAdditionalCharges(additionalCharges);
        bill.setDiscountAmount(discount);
        bill.setDiscountStrategy(strategy.getStrategyName());
        bill.setTaxAmount(tax);
        bill.setTotalAmount(total);
        return bill;
    }

    /**
     * Generates and persists a patient bill using the configured Strategy.
     */
    public BillReceiptDTO generateAndSaveBill(int appointmentId, BigDecimal additionalCharges, String strategyName, String paymentMethod, String notes) {
        AppointmentDetailDTO appointment = appointmentDAO.findDetailById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Invalid appointment ID: " + appointmentId);
        }

        BigDecimal treatmentCost = appointment.getTreatmentCost() != null ? appointment.getTreatmentCost() : BigDecimal.ZERO;
        BigDecimal consultationFee = appointment.getConsultationFee() != null ? appointment.getConsultationFee() : BigDecimal.ZERO;
        if (additionalCharges == null) additionalCharges = BigDecimal.ZERO;

        BillingStrategy strategy = BillingStrategyFactory.getStrategy(strategyName);
        BigDecimal discount = strategy.calculateDiscount(treatmentCost, consultationFee, additionalCharges);
        BigDecimal subtotal = treatmentCost.add(consultationFee).add(additionalCharges).subtract(discount);
        BigDecimal tax = strategy.calculateTax(subtotal);
        BigDecimal total = strategy.calculateTotal(treatmentCost, consultationFee, additionalCharges);

        // Check if existing bill exists for this appointment
        Bill existing = billDAO.findByAppointmentId(appointmentId);
        int billId;
        if (existing != null) {
            existing.setTreatmentCost(treatmentCost);
            existing.setConsultationFee(consultationFee);
            existing.setAdditionalCharges(additionalCharges);
            existing.setDiscountAmount(discount);
            existing.setDiscountStrategy(strategy.getStrategyName());
            existing.setTaxAmount(tax);
            existing.setTotalAmount(total);
            existing.setPaymentStatus("PAID");
            existing.setPaymentMethod(paymentMethod != null ? paymentMethod : "CASH");
            existing.setNotes(notes);
            billDAO.update(existing);
            billId = existing.getId();
        } else {
            Bill newBill = new Bill();
            newBill.setAppointmentId(appointmentId);
            newBill.setPatientId(appointment.getPatientId());
            newBill.setTreatmentCost(treatmentCost);
            newBill.setConsultationFee(consultationFee);
            newBill.setAdditionalCharges(additionalCharges);
            newBill.setDiscountAmount(discount);
            newBill.setDiscountStrategy(strategy.getStrategyName());
            newBill.setTaxAmount(tax);
            newBill.setTotalAmount(total);
            newBill.setPaymentStatus("PAID");
            newBill.setPaymentMethod(paymentMethod != null ? paymentMethod : "CASH");
            newBill.setNotes(notes);
            billId = billDAO.create(newBill);
        }

        // Update appointment status to COMPLETED automatically
        appointmentDAO.updateStatus(appointmentId, "COMPLETED");

        return billDAO.getReceiptByBillId(billId);
    }

    public BillReceiptDTO getReceiptByBillId(int billId) {
        return billDAO.getReceiptByBillId(billId);
    }

    public BillReceiptDTO getReceiptByAppointmentId(int appointmentId) {
        return billDAO.getReceiptByAppointmentId(appointmentId);
    }
}
