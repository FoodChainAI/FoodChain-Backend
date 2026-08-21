package com.example.foodchain.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Immutable read of an offer's state, shared with the orders module so it can
 * validate availability and snapshot the unit price at order-creation time
 * without depending on catalog internals.
 */
public record OfferSnapshot(
        UUID offerId,
        UUID productId,
        UUID sellerId,
        BigDecimal unitPrice,
        BigDecimal availableQuantity,
        boolean available
) {
    public boolean canSatisfy(BigDecimal quantity) {
        return available && availableQuantity.compareTo(quantity) >= 0;
    }
}
