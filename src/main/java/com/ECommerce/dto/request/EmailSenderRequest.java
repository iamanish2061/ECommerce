package com.ECommerce.dto.request;

import com.ECommerce.validation.ValidEmail;

public record EmailSenderRequest(
        @ValidEmail
        String to,
        String subject,
        String body)
{}