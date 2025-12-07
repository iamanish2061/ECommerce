package com.ECommerce.controller.admin;

import com.ECommerce.dto.request.order.UpdateOrderStatusRequest;
import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.order.OrderResponse;
import com.ECommerce.dto.response.order.SingleOrderResponse;
import com.ECommerce.model.cartandorders.OrderStatus;
import com.ECommerce.service.admin.AdminOrderService;
import com.ECommerce.validation.ValidId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    //pagination
    @GetMapping()
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(){
        List<OrderResponse> orderResponse = adminOrderService.getAllOrders();
        return ResponseEntity.ok(
                ApiResponse.ok(orderResponse, "Order fetched")
        );
    }

    @GetMapping("/status-list")
    public ResponseEntity<ApiResponse<?>> getStatusList(){
        List<String> statusList = adminOrderService.getStatusList();
        return ResponseEntity.ok(
                ApiResponse.ok(statusList, "Fetched status list")
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersOfSpeificUser(
        @ValidId @PathVariable Long userId
    ){
        List<OrderResponse> orderResponses = adminOrderService.getOrderofUser(userId);
        return ResponseEntity.ok(
                ApiResponse.ok(orderResponses, "Order of particular user fetched")
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<SingleOrderResponse>> getDetailsofOrder(
        @ValidId @PathVariable("orderId") Long orderId
    ){
        SingleOrderResponse orderResponse = adminOrderService.getDetailofOrder(orderId);
        return ResponseEntity.ok(
                ApiResponse.ok(orderResponse, "Order details fetched")
        );
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(
        @ValidId @PathVariable Long orderId,
        @Valid @RequestBody UpdateOrderStatusRequest status
    ){
        adminOrderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(
                ApiResponse.ok("Order status updated")
        );
    }



}
