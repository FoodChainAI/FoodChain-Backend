package com.example.foodchain.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String unit,
        @NotNull @DecimalMin(value = "0.0", message = "Le prix de base doit être positif.") BigDecimal basePrice
) {
}
