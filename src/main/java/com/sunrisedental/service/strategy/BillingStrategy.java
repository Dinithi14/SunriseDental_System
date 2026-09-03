package com.sunrisedental.service.strategy;

import java.math.BigDecimal;

/**
 * Strategy Pattern Interface: BillingStrategy
 * Defines the algorithm contract for calculating discounts, taxes, and final bill amounts.
 */
public interface BillingStrategy {
    
    /**
     * Calculates the discount amount based on base treatment fee, consultation fee, and additional charges.
     */
    BigDecimal calculateDiscount(BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal additionalCharges);

    /**
     * Calculates the tax amount (e.g. VAT/NBT or government health levies if applicable).
     */
    BigDecimal calculateTax(BigDecimal subtotalAfterDiscount);

    /**
     * Calculates the final net total amount.
     */
    BigDecimal calculateTotal(BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal additionalCharges);

    /**
     * Returns the human-readable strategy identifier.
     */
    String getStrategyName();

    /**
     * Returns a brief description of the applied tariff / discount scheme.
     */
    String getSchemeDescription();
}
