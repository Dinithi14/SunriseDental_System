package com.sunrisedental.service.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Health Insurance Co-Pay Strategy: Insurance policy covers 80%, patient pays 20% co-payment.
 */
public class InsuranceBillingStrategy implements BillingStrategy {

    private static final BigDecimal INSURANCE_COVERAGE_RATE = new BigDecimal("0.80");

    @Override
    public BigDecimal calculateDiscount(BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal additionalCharges) {
        BigDecimal base = treatmentCost.add(consultationFee).add(additionalCharges);
        // Discount represents insurer covered portion
        return base.multiply(INSURANCE_COVERAGE_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTax(BigDecimal subtotalAfterDiscount) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotal(BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal additionalCharges) {
        BigDecimal base = treatmentCost.add(consultationFee).add(additionalCharges);
        BigDecimal insuranceCover = calculateDiscount(treatmentCost, consultationFee, additionalCharges);
        return base.subtract(insuranceCover).setScale(2, RoundingMode.HALF_UP); // 20% patient direct co-pay
    }

    @Override
    public String getStrategyName() {
        return "INSURANCE";
    }

    @Override
    public String getSchemeDescription() {
        return "Dental Insurance Coverage (80% Insurer Covered, 20% Patient Co-Pay)";
    }
}
