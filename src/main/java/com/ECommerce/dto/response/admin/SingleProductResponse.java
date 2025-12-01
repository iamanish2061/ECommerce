package com.ECommerce.dto.response.admin;

public record SingleProductResponse(
    Long id,
    String name,
    Double price,
    String category
) {}
