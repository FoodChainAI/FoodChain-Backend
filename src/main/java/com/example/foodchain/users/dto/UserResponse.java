package com.example.foodchain.users.dto;

import com.example.foodchain.users.entity.Role;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        Role role,
        boolean verified,
        OffsetDateTime createdAt
) {
}
