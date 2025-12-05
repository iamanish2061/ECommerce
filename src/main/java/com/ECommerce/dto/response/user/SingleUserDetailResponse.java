package com.ECommerce.dto.response.user;

import com.ECommerce.model.user.Role;
import com.ECommerce.model.user.UserStatus;

import java.time.LocalDateTime;

public record SingleUserDetailResponse(
    Long id,
    String username,
    String fullname,
    String email,
    Role role,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
