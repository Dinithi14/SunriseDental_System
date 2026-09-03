package com.sunrisedental;

import com.sunrisedental.model.Bill;
import com.sunrisedental.service.BillingService;
import com.sunrisedental.service.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Billing & Strategy Pattern implementations (CIS6003 Task C).
 */
public class BillingServiceTest {

    private BillingService billingService;

    @BeforeEach
    public void setUp() {
        // Direct instantiation for pure unit testing without DB dependency
        billingService = new BillingService(null, null);
    }

    @Test
    @DisplayName("Test Standard Billing Strategy - No Discount")
    public void testStandardBillingStrategy() {
        BigDecimal treatmentCost = new BigDecimal("4500.00");
        BigDecimal consultationFee = new BigDecimal("2500.00");
        BigDecimal additionalCharges = new BigDecimal("500.00");

        Bill bill = billingService.calculateBillPreview(treatmentCost, consultationFee, additionalCharges, "STANDARD");

        assertNotNull(bill);
        assertEquals(new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP), bill.getDiscountAmount());
        assertEquals(new BigDecimal("7500.00").setScale(2, RoundingMode.HALF_UP), bill.getTotalAmount());
        assertEquals("STANDARD", bill.getDiscountStrategy());
    }

    @Test
    @DisplayName("Test Senior Citizen Discount Strategy - 10% Concession")
    public void testSeniorDiscountBillingStrategy() {
        BigDecimal treatmentCost = new BigDecimal("6000.00");
        BigDecimal consultationFee = new BigDecimal("2500.00");
        BigDecimal additionalCharges = BigDecimal.ZERO;

        Bill bill = billingService.calculateBillPreview(treatmentCost, consultationFee, additionalCharges, "SENIOR_DISCOUNT");

        // Base = 8500.00, Discount 10% = 850.00, Total = 7650.00
        assertEquals(new BigDecimal("850.00").setScale(2, RoundingMode.HALF_UP), bill.getDiscountAmount());
        assertEquals(new BigDecimal("7650.00").setScale(2, RoundingMode.HALF_UP), bill.getTotalAmount());
    }

    @Test
    @DisplayName("Test Insurance Billing Strategy - 80% Insurer Covered")
    public void testInsuranceBillingStrategy() {
        BigDecimal treatmentCost = new BigDecimal("22000.00"); // RCT
        BigDecimal consultationFee = new BigDecimal("3000.00");
        BigDecimal additionalCharges = BigDecimal.ZERO;

        Bill bill = billingService.calculateBillPreview(treatmentCost, consultationFee, additionalCharges, "INSURANCE");

        // Base = 25000.00, Covered portion (80%) = 20000.00, Patient Co-pay (20%) = 5000.00
        assertEquals(new BigDecimal("20000.00").setScale(2, RoundingMode.HALF_UP), bill.getDiscountAmount());
        assertEquals(new BigDecimal("5000.00").setScale(2, RoundingMode.HALF_UP), bill.getTotalAmount());
    }

    @Test
    @DisplayName("Test Pediatric / Child Discount Strategy - 15% Concession")
    public void testChildDiscountBillingStrategy() {
        BigDecimal treatmentCost = new BigDecimal("5000.00");
        BigDecimal consultationFee = new BigDecimal("2200.00");
        BigDecimal additionalCharges = BigDecimal.ZERO;

        Bill bill = billingService.calculateBillPreview(treatmentCost, consultationFee, additionalCharges, "CHILD_DISCOUNT");

        // Base = 7200.00, Discount 15% = 1080.00, Total = 6120.00
        assertEquals(new BigDecimal("1080.00").setScale(2, RoundingMode.HALF_UP), bill.getDiscountAmount());
        assertEquals(new BigDecimal("6120.00").setScale(2, RoundingMode.HALF_UP), bill.getTotalAmount());
    }

    @Test
    @DisplayName("Test Emergency Billing Strategy - 20% Urgent Care Surcharge")
    public void testEmergencyBillingStrategy() {
        BigDecimal treatmentCost = new BigDecimal("18000.00");
        BigDecimal consultationFee = new BigDecimal("3500.00");
        BigDecimal additionalCharges = BigDecimal.ZERO;

        Bill bill = billingService.calculateBillPreview(treatmentCost, consultationFee, additionalCharges, "EMERGENCY");

        // Base = 21500.00, Surcharge 20% = 4300.00, Total = 25800.00
        assertEquals(new BigDecimal("25800.00").setScale(2, RoundingMode.HALF_UP), bill.getTotalAmount());
    }

    @Test
    @DisplayName("Test Billing Strategy Factory Fallback Mechanism")
    public void testBillingStrategyFactory() {
        BillingStrategy strategy = BillingStrategyFactory.getStrategy("UNKNOWN_KEY");
        assertNotNull(strategy);
        assertEquals("STANDARD", strategy.getStrategyName());
    }
}
