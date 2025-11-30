package com.ECommerce.dto.request.auth;

import com.ECommerce.validation.ValidEmail;
import com.ECommerce.validation.ValidPassword;
import com.ECommerce.validation.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(

        @NotBlank(message = "Full name is required!")
        @Size(min = 4, max = 50, message = "Full name must be between 4 and 50 characters!")
        @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Full name must contain only letters and spaces!")
        String fullname,

        @ValidUsername
        String username,

        @ValidEmail
        String email,

        @NotBlank(message = "Verification code is required!")
        @Pattern(regexp = "^\\d{6}$", message = "Verification code must be exactly 6 digits!")
        String code,

        @ValidPassword
        String password,

        @NotBlank(message = "Please confirm your password!")
        @ValidPassword
        String rePassword

) {}