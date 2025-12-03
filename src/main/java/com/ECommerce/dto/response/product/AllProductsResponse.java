package com.ECommerce.dto.response.product;

import java.math.BigDecimal;

public record AllProductsResponse(
        Long id,
        String title,
        String shortDescription,
        BigDecimal price,
        Integer stock,
        String imageUrl
) {}
