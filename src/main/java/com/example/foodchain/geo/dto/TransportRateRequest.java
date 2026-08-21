package com.example.foodchain.geo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransportRateRequest(
        @NotBlank String vehicleClass,
        @NotNull @DecimalMin(value = "0.0") BigDecimal costPerKmPerTon,
        @NotNull @DecimalMin(value = "0.0") BigDecimal minCost,
        boolean active
) {
}
