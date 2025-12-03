package com.ECommerce.dto.response.product;

import java.math.BigDecimal;

public record AdminSingleProductResponse(
        SingleProductResponse s,
        BigDecimal costPrice
) {}
