package com.ECommerce.dto.request.auth;

import com.ECommerce.validation.ValidPassword;
import com.ECommerce.validation.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePasswordRequest(

        @ValidUsername
        String username,

        @NotBlank(message = "Verification code is required!")
        @Pattern(regexp = "^\\d{6}$", message = "Invalid OTP Code!")
        String code,

        @ValidPassword
        String password,

        @NotBlank(message = "Please confirm your password!")
        @ValidPassword
        String rePassword
) {}
