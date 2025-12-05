package com.ECommerce.controller.user;

import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.cart.CartResponse;
import com.ECommerce.model.user.UserPrincipal;
import com.ECommerce.service.CartService;
import com.ECommerce.validation.ValidId;
import com.ECommerce.validation.ValidQuantity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> addToCart(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @ValidId
            @PathVariable Long productId,
            @ValidQuantity
            @RequestParam(defaultValue = "1") int quantity
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Please login to add items to cart", "NOT_LOGGED_IN"));
        }

        String msg = cartService.addToCart(currentUser.getUser().getId(), productId, quantity);
        return ResponseEntity.ok(ApiResponse.ok(msg));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<CartResponse>>> getCartItems(
            @AuthenticationPrincipal UserPrincipal currentUser
    ){
        if(currentUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Please login to add items to cart", "NOT_LOGGED_IN"));
        }
        List<CartResponse> cartItems = cartService.getCartItems(currentUser.getUser().getId());
        return ResponseEntity.ok(ApiResponse.ok(
                cartItems, "Fetch successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<?>> getTotalCartCount(
        @AuthenticationPrincipal UserPrincipal currentUser
    ){
        if(currentUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Please login to add items to cart", "NOT_LOGGED_IN"));
        }
        int totalItem = cartService.getCartCount(currentUser.getUser().getId());
        Map<String, Integer> res = new HashMap<>();
        res.put("totalItem", totalItem);
        return ResponseEntity.ok(ApiResponse.ok(res, "Fetched successfully"));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> updateCart(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @ValidId @PathVariable Long productId,
            @ValidQuantity @RequestParam int quantity
    ){
        if(currentUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Please login to add items to cart", "NOT_LOGGED_IN"));
        }
        String msg = cartService.updateCart(currentUser.getUser().getId(), productId, quantity);
        return ResponseEntity.ok(ApiResponse.ok(msg));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteFromCart(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @ValidId @PathVariable Long productId
    ){
        if(currentUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Please login to add items to cart", "NOT_LOGGED_IN"));
        }

        String msg = cartService.deleteFromCart(currentUser.getUser().getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok(msg));
    }


}
