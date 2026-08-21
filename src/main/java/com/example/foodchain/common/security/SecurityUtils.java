package com.example.foodchain.common.security;

import com.example.foodchain.common.error.ApiException;
import com.example.foodchain.users.entity.Role;
import com.example.foodchain.users.security.AppUserPrincipal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Convenience accessors for the currently authenticated principal.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static AppUserPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {
            throw new ApiException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Authentification requise.");
        }
        return principal;
    }

    public static UUID currentUserId() {
        return currentPrincipal().getId();
    }

    public static Role currentRole() {
        return currentPrincipal().getRole();
    }
}
