package com.ECommerce.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailSenderRequest(
        @Email(message = "Invalid Email!")
        @NotNull(message = "Email is required!")
        String to,
        String subject,
        String body)
{}