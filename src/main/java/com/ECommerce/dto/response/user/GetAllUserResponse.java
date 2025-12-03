package com.ECommerce.dto.response.user;

import com.ECommerce.model.Role;
import com.ECommerce.model.UserStatus;

public record GetAllUserResponse(
        Long userId,
        String username,
        Role role,
        UserStatus status
){}
