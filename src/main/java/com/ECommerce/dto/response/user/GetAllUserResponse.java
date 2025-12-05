package com.ECommerce.dto.response.user;

import com.ECommerce.model.user.Role;
import com.ECommerce.model.user.UserStatus;

public record GetAllUserResponse(
        Long userId,
        String username,
        Role role,
        UserStatus status
){}
