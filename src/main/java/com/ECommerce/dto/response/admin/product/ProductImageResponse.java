package com.ECommerce.dto.response.admin.product;

public record ProductImageResponse(
    String url,
    String altText,
    boolean thumbnail
) {}
