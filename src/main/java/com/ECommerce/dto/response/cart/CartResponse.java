package com.ECommerce.dto.response.cart;

import com.ECommerce.dto.response.product.AllProductsResponse;

public record CartResponse(
    AllProductsResponse product,
    int quantity
) {}
