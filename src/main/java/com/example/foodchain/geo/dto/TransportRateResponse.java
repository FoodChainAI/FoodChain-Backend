package com.example.foodchain.geo.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransportRateResponse(
        UUID id,
        String vehicleClass,
        BigDecimal costPerKmPerTon,
        BigDecimal minCost,
        boolean active,
        OffsetDateTime validFrom
) {
}
