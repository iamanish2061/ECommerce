package com.ECommerce.dto.request.order;

import com.ECommerce.model.cartandorders.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull(message = "Order status is required.")
    OrderStatus status
) {}
