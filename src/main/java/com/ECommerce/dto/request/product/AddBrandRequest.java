package com.ECommerce.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddBrandRequest(
        @NotBlank(message = "Brand name is required.")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
        String name
) {}