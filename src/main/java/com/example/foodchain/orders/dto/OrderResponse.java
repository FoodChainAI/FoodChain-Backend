package com.example.foodchain.orders.dto;

import com.example.foodchain.orders.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID buyerId,
        OrderStatus status,
        BigDecimal total,
        OffsetDateTime createdAt,
        List<OrderLineResponse> lines
) {
}
