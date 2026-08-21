package com.example.foodchain.geo.dto;

import java.math.BigDecimal;

/**
 * Estimated transport cost between two localities. ALWAYS an estimate — the
 * {@code estimated} flag and {@code note} must be surfaced to the user, never
 * presented as a firm quote.
 */
public record TransportEstimate(
        BigDecimal distanceKm,
        BigDecimal cost,
        String vehicleClass,
        boolean estimated,
        String note
) {
    public static TransportEstimate of(BigDecimal distanceKm, BigDecimal cost, String vehicleClass) {
        return new TransportEstimate(distanceKm, cost, vehicleClass, true,
                "Estimation basée sur la distance entre chefs-lieux et un tarif indicatif ; le coût réel peut varier.");
    }
}
