package com.ECommerce.dto.response.auth;

public record AuthResponse(

        String accessToken,

        String tokenType,

        Long expiresIn,

        Long userId,

        String fullName,

        String username,

        String email

) {
    public AuthResponse(String accessToken, Long userId, String fullName,
                        String username, String email) {
        this(accessToken, "Bearer", 1800L, userId, fullName, username, email);
    }
}