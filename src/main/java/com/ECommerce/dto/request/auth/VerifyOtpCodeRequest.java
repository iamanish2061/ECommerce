package com.ECommerce.dto.request.auth;

import com.ECommerce.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpCodeRequest(
        @ValidEmail
        String email,

        @NotBlank(message = "Verification code is required!")
        @Pattern(regexp = "^\\d{6}$", message = "Verification code must be exactly 6 digits!")
        String code
) {}
