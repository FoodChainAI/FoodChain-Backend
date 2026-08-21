package com.example.foodchain.trust.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID orderId,
        UUID authorId,
        int rating,
        String comment,
        OffsetDateTime createdAt
) {
}
