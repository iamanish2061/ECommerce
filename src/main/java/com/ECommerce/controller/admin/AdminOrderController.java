package com.ECommerce.controller.admin;

import com.ECommerce.dto.request.order.UpdateOrderStatusRequest;
import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.order.OrderResponse;
import com.ECommerce.dto.response.order.SingleOrderResponse;
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
//    for returning all orders
    @GetMapping()
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(){
        List<OrderResponse> orderResponse = adminOrderService.getAllOrders();
        return ResponseEntity.ok(
                ApiResponse.ok(orderResponse, "All orders fetched")
        );
    }

//    getting status list like pending , shipped, delivered , cancelled
    @GetMapping("/status-list")
    public ResponseEntity<ApiResponse<?>> getStatusList(){
        List<String> statusList = adminOrderService.getStatusList();
        return ResponseEntity.ok(
                ApiResponse.ok(statusList, "Fetched status list")
        );
    }

//    getting all order of specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersOfSpeificUser(
        @ValidId @PathVariable Long userId
    ){
        List<OrderResponse> orderResponses = adminOrderService.getOrderOfUser(userId);
        return ResponseEntity.ok(
                ApiResponse.ok(orderResponses, "Order fetched of user: "+userId)
        );
    }

//    detail of particular order
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<SingleOrderResponse>> getDetailsOfOrder(
        @ValidId @PathVariable("orderId") Long orderId
    ){
        SingleOrderResponse orderResponse = adminOrderService.getDetailOfOrder(orderId);
        return ResponseEntity.ok(
                ApiResponse.ok(orderResponse, "Details fetched of order: "+orderId)
        );
    }

//    updating status of order
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
