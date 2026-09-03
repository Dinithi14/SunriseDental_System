package com.sunrisedental.service.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Emergency / After-Hours Dental Care Strategy: Applies a 20% surcharge for emergency room operations.
 */
public class EmergencyBillingStrategy implements BillingStrategy {

    private static final BigDecimal SURCHARGE_RATE = new BigDecimal("0.20");

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
        BigDecimal surcharge = base.multiply(SURCHARGE_RATE).setScale(2, RoundingMode.HALF_UP);
        return base.add(surcharge).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "EMERGENCY";
    }

    @Override
    public String getSchemeDescription() {
        return "Emergency Priority Care (20% Emergency Facility Surcharge)";
    }
}
