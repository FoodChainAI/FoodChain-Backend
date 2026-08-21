package com.example.foodchain.catalog.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OfferResponse(
        UUID id,
        UUID sellerId,
        boolean sellerVerified,
        ProductResponse product,
        BigDecimal quantity,
        BigDecimal price,
        boolean available,
        String location,
        OffsetDateTime createdAt
) {
}
