package com.sunrisedental.service.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory Pattern: BillingStrategyFactory
 * Creates and returns the appropriate BillingStrategy instance dynamically based on strategy key.
 */
public class BillingStrategyFactory {

    private static final Map<String, BillingStrategy> STRATEGY_REGISTRY = new HashMap<>();

    static {
        registerStrategy("STANDARD", new StandardBillingStrategy());
        registerStrategy("SENIOR_DISCOUNT", new SeniorDiscountBillingStrategy());
        registerStrategy("INSURANCE", new InsuranceBillingStrategy());
        registerStrategy("CHILD_DISCOUNT", new ChildDiscountBillingStrategy());
        registerStrategy("EMERGENCY", new EmergencyBillingStrategy());
    }

    public static void registerStrategy(String key, BillingStrategy strategy) {
        STRATEGY_REGISTRY.put(key.toUpperCase(), strategy);
    }

    public static BillingStrategy getStrategy(String strategyName) {
        if (strategyName == null || strategyName.trim().isEmpty()) {
            return STRATEGY_REGISTRY.get("STANDARD");
        }
        BillingStrategy strategy = STRATEGY_REGISTRY.get(strategyName.trim().toUpperCase());
        return (strategy != null) ? strategy : STRATEGY_REGISTRY.get("STANDARD");
    }

    public static Map<String, BillingStrategy> getAllStrategies() {
        return STRATEGY_REGISTRY;
    }
}
