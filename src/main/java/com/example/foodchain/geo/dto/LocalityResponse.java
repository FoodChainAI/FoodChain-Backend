package com.example.foodchain.geo.dto;

import com.example.foodchain.geo.entity.LocalityLevel;
import java.math.BigDecimal;
import java.util.UUID;

public record LocalityResponse(
        UUID id,
        String name,
        LocalityLevel level,
        UUID parentId,
        BigDecimal latitude,
        BigDecimal longitude,
        boolean approximate
) {
}
