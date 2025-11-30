package com.ECommerce.dto.request.auth;

import com.ECommerce.validation.ValidPassword;
import com.ECommerce.validation.ValidUsername;

public record LoginRequest(

        @ValidUsername
        String username,

        @ValidPassword()
        String password
) {}
