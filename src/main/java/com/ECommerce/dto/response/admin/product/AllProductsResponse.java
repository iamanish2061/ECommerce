package com.ECommerce.dto.response.admin.product;

import java.math.BigDecimal;

public record AllProductsResponse(
        Long id,
        String sku,
        String title,
        String shortDescription,
        BigDecimal price,
        Integer stock,
        String imageUrl
) {}
