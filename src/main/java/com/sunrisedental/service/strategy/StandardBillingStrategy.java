package com.sunrisedental.service.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Standard billing with 0% discount and standard calculation.
 */
public class StandardBillingStrategy implements BillingStrategy {

    @Override
    public BigDecimal calculateDiscount(BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal additionalCharges) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTax(BigDecimal subtotalAfterDiscount) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotal(BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal additionalCharges) {
        BigDecimal base = treatmentCost.add(consultationFee).add(additionalCharges);
        BigDecimal discount = calculateDiscount(treatmentCost, consultationFee, additionalCharges);
        BigDecimal subtotal = base.subtract(discount);
        BigDecimal tax = calculateTax(subtotal);
        return subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "STANDARD";
    }

    @Override
    public String getSchemeDescription() {
        return "Standard Clinic Tariff (No special discount)";
    }
}
