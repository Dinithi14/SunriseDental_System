package com.sunrisedental.service.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Senior Citizen Discount Strategy: 10% discount on total treatment + consultation charges.
 */
public class SeniorDiscountBillingStrategy implements BillingStrategy {

    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");

    @Override
    public BigDecimal calculateDiscount(BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal additionalCharges) {
        BigDecimal base = treatmentCost.add(consultationFee).add(additionalCharges);
        return base.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTax(BigDecimal subtotalAfterDiscount) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotal(BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal additionalCharges) {
        BigDecimal base = treatmentCost.add(consultationFee).add(additionalCharges);
        BigDecimal discount = calculateDiscount(treatmentCost, consultationFee, additionalCharges);
        return base.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "SENIOR_DISCOUNT";
    }

    @Override
    public String getSchemeDescription() {
        return "Senior Citizen Scheme (10% overall discount)";
    }
}
