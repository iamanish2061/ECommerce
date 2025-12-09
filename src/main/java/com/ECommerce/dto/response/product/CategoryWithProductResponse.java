package com.ECommerce.dto.response.product;

import java.util.List;

public record CategoryWithProductResponse(
   CategoryResponse categoryResponse,
   List<AllProductsResponse> allProductsResponse
) {}
