package com.ECommerce.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VerifyOtpCodeRequest(
        @NotBlank(message = "Email is required!")
        @Email(message = "Please provide a valid email address!")
        @Size(max = 100, message = "Email is too long!")
        String email,

        @NotBlank(message = "Verification code is required!")
        @Pattern(regexp = "^\\d{6}$", message = "Verification code must be exactly 6 digits!")
        String code
) {}
