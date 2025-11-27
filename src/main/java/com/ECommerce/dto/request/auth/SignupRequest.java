package com.ECommerce.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "Fullname is required!")
        @Size(min = 4, message = "Fullname must be at least 4 characters!")
        String fullname,

        @NotBlank(message = "Username is required!")
        @Size(min = 4, message = "Username must be at least 4 characters!")
        String username,

        @NotBlank(message = "Email is required!")
        @Email(message = "Invalid Email!")
        String email,

        @NotBlank(message = "Code is required!")
        @Size(min = 6, max = 6, message = "Invalid code!" )
        String code,

        @NotBlank(message = "Password is required!")
        String password,


        @NotBlank(message = "Please enter password twice!")
        String rePassword

) {
}
