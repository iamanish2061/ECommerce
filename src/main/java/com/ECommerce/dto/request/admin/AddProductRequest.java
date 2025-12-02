package com.ECommerce.dto.request.admin;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record AddProductRequest(
        @NotBlank(message = "SKU is required")
        @Size(max = 60)
        String sku,

        @NotBlank(message = "Title is required")
        @Size(max = 255)
        String title,

        @Size(max = 500)
        String shortDescription,

        String description,

        @NotBlank(message = "Brand Name is required")
        String brandName,

        @NotBlank(message = "Category Name is required")
        String categoryName,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal basePrice,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal sellingPrice,

        @Min(0)
        Integer stock,

        @Size(max = 30)
        String sizeMl, // "50ml", "100ml", "200g"

        boolean active,

        // Tags — send as list of tag slugs (e.g. "beard-growth", "sulfate-free")
        List<String> tagSlugs,

        // Images — admin uploads or pastes URLs
        @NotEmpty(message = "At least one image required")
        List<AddProductImageRequest> images
) {}
