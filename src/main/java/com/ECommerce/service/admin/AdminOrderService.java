package com.ECommerce.service.admin;

import com.ECommerce.dto.request.order.UpdateOrderStatusRequest;
import com.ECommerce.dto.response.order.OrderItemResponse;
import com.ECommerce.dto.response.order.OrderResponse;
import com.ECommerce.dto.response.order.SingleOrderResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.cartandorders.OrderItem;
import com.ECommerce.model.cartandorders.OrderModel;
import com.ECommerce.repository.cartAndOrders.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;

    public List<OrderResponse> getAllOrders() {
        List<OrderModel> allOrders = orderRepository.findAll();
        return allOrders.stream()
                .map(o->new OrderResponse(
                        o.getId(),
                        o.getUserId(),
                        o.getTotalAmount(),
                        o.getStatus(),
                        o.getCreatedAt()
                ))
                .toList();
    }

    public List<String> getStatusList() {
        return Arrays.asList("PENDING", "PAID", "SHIPPED", "DELIVERED", "CANCELLED");
    }

    public List<OrderResponse> getOrderofUser(Long userId) {
        List<OrderModel> allOrderOfUser = orderRepository.findByUserId(userId);
        if(allOrderOfUser == null){
            return new ArrayList<>();
        }
        return allOrderOfUser.stream()
                .map(o->new OrderResponse(
                        o.getId(),
                        o.getUserId(),
                        o.getTotalAmount(),
                        o.getStatus(),
                        o.getCreatedAt()
                ))
                .toList();
    }

    public SingleOrderResponse getDetailofOrder(Long orderId) {
        OrderModel order = orderRepository.findById(orderId).orElseThrow(
                ()->new ApplicationException("Order not found with id: "+orderId, "INVALID_ORDER_ID", HttpStatus.BAD_REQUEST)
        );

        return new SingleOrderResponse(
                new OrderResponse(
                        order.getId(),
                        order.getUserId(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getCreatedAt()
                ),
                order.getOrderItems().stream()
                        .map(oi-> new OrderItemResponse(
                                oi.getProductId(),
                                oi.getProduct().getTitle(),
                                oi.getQuantity(),
                                oi.getPriceAtPurchase(),
                                oi.getPriceAtPurchase().multiply(BigDecimal.valueOf(oi.getQuantity()))
                        )).toList()
        );
    }

    @Transactional
    public void updateOrderStatus(Long orderId, UpdateOrderStatusRequest status) {
    }
}
