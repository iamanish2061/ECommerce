package com.ECommerce.dto.response.order;

import com.ECommerce.model.cartandorders.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
   Long orderId,
   Long userId,
   BigDecimal totalAmount,
   OrderStatus status,
   LocalDateTime orderDate
) {}
