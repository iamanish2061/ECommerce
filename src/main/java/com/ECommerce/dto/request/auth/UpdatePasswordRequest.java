package com.ECommerce.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(

        @NotBlank(message = "Username is required!")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters!")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Username can only contain letters, numbers, and underscores!")
        String username,

        @NotBlank(message = "Email is required!")
        @Email(message = "Please provide a valid email address!")
        @Size(max = 100, message = "Email is too long!")
        String email,

        @NotBlank(message = "Verification code is required!")
        @Pattern(regexp = "^\\d{6}$", message = "Verification code must be exactly 6 digits!")
        String code,

        @NotBlank(message = "Password is required!")
        @Size(min = 8, max = 50, message = "Password must be at least 8 characters!")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!_*])(?=\\S+$).{8,}$",
                message = "Password must contain at least one letter, one number, and one special character (@#$%^&+=!*_)!"
        )
        String password,

        @NotBlank(message = "Please confirm your password!")
        String rePassword
) {}
