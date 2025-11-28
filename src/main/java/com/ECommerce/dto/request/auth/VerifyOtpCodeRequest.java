package com.ECommerce.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record VerifyOtpCodeRequest(

        @Email(message = "Invalid Email")
        String email,

        @Size(min = 6, max = 6, message = "Invalid Code")
        String code
) {
}
