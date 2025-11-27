package com.ECommerce.dto.request.auth;

public record VerifyOtpCodeRequest(

        String email,

        String code
) {
}
