package com.example.foodchain.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String category,
        String unit,
        BigDecimal basePrice
) {
}
