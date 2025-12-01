package com.ECommerce.dto.response.admin;

public record AllProductsResponse(
        Long id,
        String name,
        Double price,
        boolean inStock
) {}
