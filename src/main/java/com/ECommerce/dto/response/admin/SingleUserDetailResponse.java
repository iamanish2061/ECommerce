package com.ECommerce.dto.response.admin;

import com.ECommerce.model.Role;
import com.ECommerce.model.UserStatus;

import java.time.LocalDateTime;

public record SingleUserDetailResponse(
    Long id,
    String username,
    String fullname,
    String email,
    Role role,
    UserStatus status,
    LocalDateTime createdAt
) {}
