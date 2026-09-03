package com.sunrisedental.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Bill {
    private int id;
    private String billNumber;
    private int appointmentId;
    private int patientId;
    private BigDecimal treatmentCost;
    private BigDecimal consultationFee;
    private BigDecimal additionalCharges;
    private BigDecimal discountAmount;
    private String discountStrategy; // STANDARD, SENIOR_DISCOUNT, INSURANCE, CHILD_DISCOUNT, EMERGENCY
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String paymentStatus; // PENDING, PAID, CANCELLED
    private String paymentMethod; // CASH, CARD, INSURANCE, ONLINE
    private Timestamp billingDate;
    private String notes;

    public Bill() {}

    public Bill(int id, String billNumber, int appointmentId, int patientId, BigDecimal treatmentCost,
                BigDecimal consultationFee, BigDecimal additionalCharges, BigDecimal discountAmount,
                String discountStrategy, BigDecimal taxAmount, BigDecimal totalAmount, String paymentStatus,
                String paymentMethod, Timestamp billingDate, String notes) {
        this.id = id;
        this.billNumber = billNumber;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.additionalCharges = additionalCharges;
        this.discountAmount = discountAmount;
        this.discountStrategy = discountStrategy;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.billingDate = billingDate;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public BigDecimal getTreatmentCost() { return treatmentCost; }
    public void setTreatmentCost(BigDecimal treatmentCost) { this.treatmentCost = treatmentCost; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public BigDecimal getAdditionalCharges() { return additionalCharges; }
    public void setAdditionalCharges(BigDecimal additionalCharges) { this.additionalCharges = additionalCharges; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public String getDiscountStrategy() { return discountStrategy; }
    public void setDiscountStrategy(String discountStrategy) { this.discountStrategy = discountStrategy; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Timestamp getBillingDate() { return billingDate; }
    public void setBillingDate(Timestamp billingDate) { this.billingDate = billingDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
