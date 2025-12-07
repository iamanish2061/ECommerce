package com.ECommerce.dto.response.order;

import java.util.List;

public record SingleOrderResponse(
    OrderResponse orderResponse,
    List<OrderItemResponse> orderItems
) {}
