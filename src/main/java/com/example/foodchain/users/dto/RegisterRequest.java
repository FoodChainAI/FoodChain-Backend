package com.example.foodchain.users.dto;

import com.example.foodchain.users.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100, message = "Le mot de passe doit contenir au moins 8 caractères.") String password,
        @NotNull Role role
) {
}
