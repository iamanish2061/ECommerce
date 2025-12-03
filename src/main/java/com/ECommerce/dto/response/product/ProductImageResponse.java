package com.ECommerce.dto.response.product;

public record ProductImageResponse(
    String url,
    String altText,
    boolean thumbnail
) {}
