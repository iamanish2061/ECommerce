package com.ECommerce.dto.response.product;

import java.util.List;

public record TagWithProductResponse(
        TagResponse tagResponse,
        List<AllProductsResponse> allProductsResponse
) {}
