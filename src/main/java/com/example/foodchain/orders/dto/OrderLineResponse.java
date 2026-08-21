package com.example.foodchain.orders.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderLineResponse(
        UUID id,
        UUID offerId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
