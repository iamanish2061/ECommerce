package com.ECommerce.dto.response.order;

import java.math.BigDecimal;

public record OrderItemResponse (
    Long productId,
    String productName,
    int quantity,
    BigDecimal priceAtPurchase,
    BigDecimal totalAmount,
    String imageUrl
){}
