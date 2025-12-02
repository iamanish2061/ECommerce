package com.ECommerce.dto.response.admin.product;

import java.math.BigDecimal;
import java.util.List;

public record SingleProductResponse(
    Long id,
    String title,
    String shortDescription,
    String description,
    BrandResponse brand,
    CategoryResponse category,
    BigDecimal price,
    Integer stock,
    String sizeMl,
    List<TagResponse> tags,
    List<ProductImageResponse> images
) {}




