package com.ECommerce.dto.response.product;

import java.util.List;

public record BrandWithProductResponse(
    BrandResponse brandResponse,
    List<AllProductsResponse> products
) {}
