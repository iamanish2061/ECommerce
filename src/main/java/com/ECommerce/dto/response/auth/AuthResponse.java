package com.ECommerce.dto.response.auth;

import com.ECommerce.model.user.Role;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        Long userId,
        String fullName,
        String username,
        String email,
        Role role
) {
    public AuthResponse(String accessToken, Long userId, String fullName,
                        String username, String email, Role role) {
        this(accessToken, "Bearer", 1800L, userId, fullName, username, email, role);
    }
}